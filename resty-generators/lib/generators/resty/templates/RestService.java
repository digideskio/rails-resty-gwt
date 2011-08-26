package <%= restservices_package %>;

import <%= gwt_rails_package %>.dispatchers.RestfulDispatcherSingleton;
<% if action_map.values.member? :get_all -%>
import <%= gwt_rails_package %>.dispatchers.DefaultDispatcherSingleton;
import java.util.List;
<% end -%>

import javax.ws.rs.*;

import org.fusesource.restygwt.client.*;

<% if name -%>
import <%= models_package %>.*;
<% end -%>

<% if options[:singleton] -%>@Path("/<%= singular_table_name %>")<% end %>
@Options(dispatcher = RestfulDispatcherSingleton.class)
public interface <%= controller_class_name %>RestService extends RestService {

<% actions.each do |action| 
     case action_map[action]
     when :get_all -%>
  @GET @Path("/<%= table_name %>")
  @Options(dispatcher = DefaultDispatcherSingleton.class)
  void <%= action %>(MethodCallback<List<<%= class_name %>>> callback);

//  @GET @Path("/<%= table_name %>")
//  void <%= action %>(MethodCallback<List<<%= class_name %>>> callback, @QueryParam("limit") int limit, @QueryParam("offset") int offset);
//
<%   when :get_single -%>
  @GET<% unless options[:singleton] -%> @Path("/<%= table_name %>/{id}")<% end %>
  void <%= action %>(<% unless options[:singleton] -%>@PathParam("id") int id, <% end -%>MethodCallback<<%= class_name %>> callback);

<%   when :post -%>
  @POST @Path("/<%= table_name %>")
  void <%= action %>(<%= class_name %> value, MethodCallback<<%= class_name %>> callback);

<%   when :put -%>
  @PUT<% unless options[:singleton] -%> @Path("/<%= table_name %>/{id}")<% end %>
  void <%= action %>(<% unless options[:singleton] -%>@PathParam("id") @Attribute("id") <% end -%><%= class_name %> value, MethodCallback<<%= class_name %>> callback);

<%   when :delete -%>
  @DELETE @Path("/<%= table_name %>/{id}")
  void <%= action %>(@PathParam("id") @Attribute("id") <%= class_name %> value, MethodCallback<Void> callback);

<%   else -%>
  @GET @Path("/<%= table_name %>/<%= action %>")
  void <%= action %>(MethodCallback<Void> callback);

<% end
end -%>
}
