<?php
$data=json_decode(file_get_contents("data.json"));
$data_array = array();
foreach($data as $item){
    $std_item = array();
    $std_item["standard"] = $item->standard;
    $board_array = array();
    foreach($item->boards as $board){
        array_push($board_array, $board->board);
    }
    $std_item["boards"] = $board_array;
    array_push($data_array, $std_item);
}

echo json_encode($data_array);
?>