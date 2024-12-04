<?php

$servidor = 'localhost';
$banco = 'bdeventos';
$usuario = 'root';
$senha = '';

$conexao = mysqli_connect($servidor, $usuario, $senha, $banco);

$json = file_get_contents('php://input');
$obj = json_decode($json);

$texto1 = $obj->nome;
$texto2 = $obj->senha;

$sql = "INSERT INTO usuario(nome, senha) VALUES ('$texto1', '$texto2')";
mysqli_query($conexao,$sql);
?>