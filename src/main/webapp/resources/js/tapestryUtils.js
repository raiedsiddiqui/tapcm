/**
* @desc Common functions used in the project, like confirmDelete...
* @author lxie
* 
*/
function confirmDelete()
{
	var x = confirm("Are you sure you want to delete?");
	if (x)
		return true;
	else
		return false;
}