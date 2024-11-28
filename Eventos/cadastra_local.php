<?php
$servidor = 'localhost';
$banco = 'bdeventos';
$usuario = 'root';
$senha = '';

$json = file_get_contents('php://input');
$obj = json_decode($json);

$conexao = mysqli_connect($servidor, $usuario, $senha, $banco);

if (!$conexao) {
    echo json_encode([
        "status" => "erro",
        "mensagem" => "Falha na conexão com o banco de dados: " . mysqli_connect_error()
    ]);
    exit();
}

$texto1 = $obj->nome;
$texto2 = $obj->endereco;
$texto3 = $obj->capacidade;

if (empty($texto1) || empty($texto2) || empty($texto3)) {
    echo json_encode([
        "status" => "erro",
        "mensagem" => "Os campos nome, endereço e capacidade são obrigatórios."
    ]);
    exit();
}

$sql = "INSERT INTO locais (nome_local, endereco, capacidade) VALUES ('$texto1', '$texto2', '$texto3')";

if (mysqli_query($conexao, $sql)) {
    echo json_encode([
        "status" => "sucesso",
        "mensagem" => "Local cadastrado com sucesso!"
    ]);
} else {
    echo json_encode([
        "status" => "erro",
        "mensagem" => "Erro ao cadastrar local: " . mysqli_error($conexao)
    ]);
}

mysqli_close($conexao);
?>
