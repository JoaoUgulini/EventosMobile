<?php

$servidor = 'localhost';
$banco = 'bdeventos';
$usuario = 'root';
$senha = '';

$json = file_get_contents('php://input');
$obj = json_decode($json, true); // Decodifica o JSON enviado

$conexao = mysqli_connect($servidor, $usuario, $senha, $banco);

if (!$conexao) {
    echo json_encode([
        "status" => "erro",
        "mensagem" => "Falha na conexão com o banco de dados: " . mysqli_connect_error()
    ]);
    exit();
}

$id_usuario = intval($obj['id_usuario']); // Converte para número inteiro
$id_local = intval($obj['id_local']);     // Converte para número inteiro
$data = mysqli_real_escape_string($conexao, $obj['data']); // Sanitiza a entrada
$hora = mysqli_real_escape_string($conexao, $obj['hora']); // Sanitiza a entrada

$data_mysql = DateTime::createFromFormat('d/m/Y', $data)->format('Y-m-d');

$query = "INSERT INTO eventos (id_usuario, id_local, data_evento, hora_evento) VALUES (?, ?, ?, ?)";
$stmt = mysqli_prepare($conexao, $query);
mysqli_stmt_bind_param($stmt, "iiss", $id_usuario, $id_local, $data_mysql, $hora);

if (mysqli_stmt_execute($stmt)) {
    echo json_encode([
        "status" => "sucesso",
        "mensagem" => "Evento registrado com sucesso."
    ]);
} else {
    echo json_encode([
        "status" => "erro",
        "mensagem" => "Erro ao registrar evento: " . mysqli_error($conexao)
    ]);
}

// Fecha a conexão com o banco de dados
mysqli_close($conexao);

?>
