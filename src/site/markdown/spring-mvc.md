Spring MVC
================

Tapestry uses Spring MVC to handle page navigation. This allows for "prettified" urls ("/login" rather than "/login.jsp")
and more flexible navigation rules.

Controller
----------
Page views are defined in src/main/java/org/tapestry/controllers/TapestryController.java
Controller settings are in WEB-INF/mvc-dispatcher-servlet.xml

Adding views
------------
To add a new view, use the following code snippet:
`@RequestMapping(value="/target", method=request.GET)
public String functionName([args]){
	//Any interesting java code here
	return "filename";
}`
This should return the name (with no extension) of the file to open. The extension will be added automatically as configured
in mvc-dispatcher-servlet.xml (in this case, ".jsp").
Args are surprisingly flexible for a Java function. Add:
* "ModelMap model" if you want to inject data into a page. Then, in your code, call `model.add_attribute('name', val)` before returning the page. The page template can use ${name} to access the value.
* "SecurityContextHolderAwareRequestWrapper request" if you need information about the current user. Then, in your code, call `request.isUserInRole(role)` to check authentication, or `request.getPrinciple()` to get the user Principle object.
