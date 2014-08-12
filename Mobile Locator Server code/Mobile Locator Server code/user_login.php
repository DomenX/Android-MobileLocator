
<?php

@$connection=mysqli_connect('localhost','root','','mysql');
$connection->select_db('mysql');

@$w_user=$_POST['username'];
@$w_pass=$_POST['password'];
$value;
@session_start();
			
$query = 'SELECT * from mb_admin WHERE admin_username LIKE \''.$w_user.'\' AND admin_password LIKE\''.$w_pass.'\';';
$result = mysqli_query($connection,$query);

if (mysqli_num_rows($result)>0)
{
	$value=mysqli_fetch_object($result);
	$_SESSION['user_id']=$value->admin_id;
	$_SESSION['user_name']=$value->admin_username;
	$_SESSION['password']=md5($value->admin_password);
	
	header('Location:./validatelocation.php');
	exit();
}
else
{
	header('Location:./login.php?fail=1');
	exit();
}	
?>
