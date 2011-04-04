require 'rails'

module Resty
  class RestyRailtie < Rails::Railtie

    config.generators do
      require 'rails/generators'      
      require 'rails/generators/rails/controller/controller_generator'
      #require 'rails/generators/erb/scaffold/scaffold_generator'
      Rails::Generators::ControllerGenerator.hook_for :resty, :type => :boolean, :default => true do |controller|
        invoke controller, [ class_name, actions ]
      end
      #Erb::Generators::ScaffoldGenerator.source_paths.insert(0, File.expand_path('../../generators/ixtlan/templates', __FILE__))
    end

    config.after_initialize do
      ActiveRecord::Base.include_root_in_json = false
      # TODO there migt be a way to tell ALL ActiveModel:
      #ActiveModel::Base.include_root_in_json = false

      # get the time/date format right ;-) and match it with resty
      class DateTime
        def as_json(options = nil)
          strftime('%Y-%m-%dT%H:%M:%S.%s%z')
        end
      end
      class ActiveSupport::TimeWithZone
        def as_json(options = nil)
          strftime('%Y-%m-%dT%H:%M:%S.%s%z')
        end
      end
      class Date
        def as_json(options = nil)
          strftime('%Y-%m-%dT%H:%M:%S.%s%z')
        end
      end
      class Time
        def as_json(options = nil)
          strftime('%Y-%m-%dT%H:%M:%S.%s%z')
        end
      end
    end
  end
end