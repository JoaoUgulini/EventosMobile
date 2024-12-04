<?php
$servidor = 'localhost';
$banco = 'bdeventos';
$usuario = 'root';
$senha = '';

$conexao = mysqli_connect($servidor, $usuario, $senha, $banco);

$json = file_get_contents('php://input');
file_put_contents('log.txt', $json . PHP_EOL, FILE_APPEND);

$obj = json_decode($json);

$texto1 = $obj->nome_local;
$texto2 = $obj->endereco;
$texto3 = $obj->capacidade;

$sql = "INSERT INTO locais(nome_local, endereco, capacidade) VALUES ('$texto1', '$texto2', '$texto3')";

mysqli_query($conexao,$sql);
?>
