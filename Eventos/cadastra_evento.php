<?php

    $servidor = 'localhost';
    $banco = 'bdeventos';
    $usuario = 'root';
    $senha = '';

    $conexao = mysqli_connect($servidor, $usuario, $senha, $banco);

    $json = file_get_contents('php://input');
$obj = json_decode($json);
file_put_contents('logEvento.txt', $json . PHP_EOL, FILE_APPEND);


        $texto1 = (int) $obj-> id_usuario;
        $texto2 = (int) $obj-> id_local;
        $texto3 = $obj-> nome_evento;
        $texto4 = $obj-> data;
        $texto5 = $obj-> hora;

        $sql = "INSERT INTO eventos (id_usuario, id_local,nome_evento, data_evento, hora_evento) 
                VALUES ('".$texto1."','".$texto2."','".$texto3."','".$texto4."','".$texto5."')";
        mysqli_query($conexao,$sql);

?>

