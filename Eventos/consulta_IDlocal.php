<?php
$servidor = 'localhost';
$banco = 'bdeventos';
$usuario = 'root';
$senha = '';

$json = file_get_contents('php://input');
$obj = json_decode($json);
file_put_contents('logLocal.txt', $json . PHP_EOL, FILE_APPEND);

$texto1 = $obj->nome_local;

$conexao = mysqli_connect($servidor, $usuario, $senha, $banco);

$query = "SELECT id FROM locais WHERE nome_local = '$texto1'";
$dados = mysqli_query($conexao, $query);

if ($dados) {
    $data = mysqli_fetch_assoc($dados);
    if ($data) {
        $id = $data['id'];
        file_put_contents('logResLocal.txt', "ID encontrado: " . $id);
        echo json_encode(["id" => $id]);
    }
}

mysqli_close($conexao);
