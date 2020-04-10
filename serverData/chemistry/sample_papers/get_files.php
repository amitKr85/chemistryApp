<?php

if(isset($_GET["std"]) && isset($_GET["board"]) && isset($_GET["lang"])){
	$std_value = $_GET["std"];
	$board_value = $_GET["board"];
	$lang_value = $_GET["lang"];
	if($std_value<9 || $std_value>12 || (!($lang_value=="en") && !($lang_value=="hi")))
	    die("std value error");
}
else
	die("args error");
	
$data_item=json_decode(file_get_contents("data.json"))[$std_value-9];
$files_array = array();
foreach($data_item->boards as $board_item){
    
    if($board_item->board == $board_value){
        foreach ($board_item->languages as $language_item) {
        	
        	if($language_item->language == $lang_value){
        		foreach($language_item->files as $file_item){
		            array_push($files_array, $file_item);
		        }
		        break;
        	}

        }
        
        break;
    }
}
echo json_encode($files_array);

?>