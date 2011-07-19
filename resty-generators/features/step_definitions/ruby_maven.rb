require 'fileutils'

module Maven
  class RubyMaven

    # make the command line for the goals of the jruby-maven-plugins nicer
    PLUGINS = {
      :rake => [:rake],
      :ruby => [:jruby, :compile],
      :gem => [:package, :install, :push, :exec, :pom, :initialize, :irb],
      :gemify => [:gemify, :versions],
      :rails2 => [:new, :generate, :rake, :server, :console],
      :rails3 => [:new, :generate, :rake, :server, :console, :dbconsole, :pom, :initialize],
      :cucumber => [:test],
      :rspec => [:test],
      :runit => [:test],
      :bundler => [:install]
    }
    ALIASES = {
      :jruby => :ruby, 
      :spec => :rspec, 
      :rails => :rails3, 
      :bundle => :bundler
    }

    def initialize
      @command = "#{ENV['GEM_HOME']}/bin/rmvn"
      @jruby = File.read(@command).split("\n")[0].sub(/^#!/, '')
      @maven_home = File.expand_path(Dir.glob("#{ENV['GEM_HOME']}/gems/ruby-maven-*-java")[0])
    end

    def launch_jruby(args)
      classpath_array.each do |path|
        require path
      end

      java.lang.System.setProperty("classworlds.conf", 
                                   File.join(@maven_home, 'bin', "m2.conf"))

      java.lang.System.setProperty("maven.home", @maven_home)

      org.codehaus.plexus.classworlds.launcher.Launcher.main(args)
    end

    def classpath_array
      (Dir.glob(File.join(@maven_home, "boot", "*jar")) + 
       Dir.glob(File.join(@maven_home, "ext", "ruby-tools*jar"))).each do |path|
        path
      end
    end
    
    def launch_java(*args)
      "java -cp #{classpath_array.join(':')} -Dmaven.home=#{File.expand_path(@maven_home)} -Dclassworlds.conf=#{File.expand_path(File.join(@maven_home, 'bin', 'm2.conf'))} org.codehaus.plexus.classworlds.launcher.Launcher #{args.join ' '}"
    end
       
    def prepare(args)
      if args.size > 0 
        name = args[0].to_sym
        name = ALIASES[name] || name
        if PLUGINS.member?(name)
          start = 1
          if args.size > 1
            if PLUGINS[name].member? args[1].to_sym
              goal = args[1].to_sym
              start = 2
            else
              goal = PLUGINS[name][0]
            end
          else
            goal = PLUGINS[name][0]
          end
          aa = if index = args.index("--")
                 args[(index + 1)..-1]
               else
                 []
               end
          ruby_args = (args[start, (index || 1000) - start] || []).join(' ')

          # determine the version and delete from args if given
          version = args.detect do |a|
            a =~ /^-Dplugin.version=/
          end
          if version
            aa.delete(version)
            version.sub!(/^-Dplugin.version=/, ':')
          end
          aa << "de.saumya.mojo:#{name}-maven-plugin#{version}:#{goal}"
          aa << "-Dargs=#{ruby_args}" if ruby_args.size > 0
          args.replace(aa)
        else
          args.delete("--")
        end
      end
      args
    end

    def log(args)
      log = File.join('log', 'rmvn.log')
      if File.exists? File.dirname(log)
        File.open(log, 'a') do |f|
          f.puts args.join ' '
        end
      end
    end

    def maybe_print_help(args)
      if args.size == 0 || args[0] == "--help"
        puts "usage: rmvn [<plugin name>|<plugin alias> [<args>] [-- <maven options>] | [<maven goal>|<maven phase> <maven options>] | --help"
        PLUGINS.each do |name, goals|
          puts
          print "plugin #{name}"
          print " - alias: #{ALIASES[name]}" if ALIASES[name]
          puts
          if goals.size > 1
            print "\tgoals       : #{goals.join(',')}"
            puts
          end
          print "\tdefault goal: #{goals[0]}"
          puts
        end
        puts
        ["--help"]
      else
        args
      end
    end

    def options
      @options ||= {}
    end
    
    def options_string
      options_array.join ' '
    end
    
    def options_array
      options.collect do |k,v|
        if k =~ /^-D/
          v = "=#{v}" if v
        else
          v = " #{v}" if v
        end
        "#{k}#{v}"
      end
    end

    def command_line(args)
      args = prepare(args)
      args = maybe_print_help(args)
      args
    end

    def exec(*args)
      a = command_line(args.dup.flatten)
      a << options_array
      a.flatten!
      #puts a.join ' '
      #launch_jruby(a)
      full = "#{@jruby} #{@command} #{args.join ' '} -- #{options_string}"
      system full
    end

    def exec_in(launchdirectory, *args)
      FileUtils.cd(launchdirectory) do
        exec(args)
      end
    end
  end
end