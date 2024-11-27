<?php

$servidor = 'localhost';
$banco = 'bdeventos';
$usuario = 'root';
$senha = '';

$conexao = mysqli_connect($servidor, $usuario, $senha, $banco);

if (!$conexao) {
    echo json_encode([
        "status" => "erro",
        "mensagem" => "Falha na conexão com o banco de dados."
    ]);
    exit();
}

$json = file_get_contents('php://input');
$obj = json_decode($json);

$texto1 = $obj->nome;
$texto2 = $obj->senha;

if (empty($texto1) || empty($texto2)) {
    echo json_encode([
        "status" => "erro",
        "mensagem" => "Os campos nome e senha são obrigatórios."
    ]);
    exit();
}

$sql = "INSERT INTO usuario(nome, senha) VALUES ('$texto1', '$texto2')";

if (mysqli_query($conexao, $sql)) {
    echo json_encode([
        "status" => "sucesso",
        "mensagem" => "Usuário cadastrado com sucesso."
    ]);
} else {
    echo json_encode([
        "status" => "erro",
        "mensagem" => "Erro ao cadastrar usuário: " . mysqli_error($conexao)
    ]);
}

mysqli_close($conexao);

?>s