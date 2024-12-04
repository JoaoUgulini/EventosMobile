<?php

$servidor = 'localhost';
$banco = 'bdeventos';
$usuario = 'root';
$senha = '';

// Conexão ao banco de dados
$conexao = mysqli_connect($servidor, $usuario, $senha, $banco);

if (!$conexao) {
    http_response_code(500);
    echo json_encode([
        "status" => "erro",
        "mensagem" => "Falha na conexão com o banco de dados: " . mysqli_connect_error()
    ]);
    exit();
}

// Consulta aos locais
$query = "SELECT id, nome, capacidade FROM locais";
$result = mysqli_query($conexao, $query);

if ($result) {
    $locais = [];
    while ($row = mysqli_fetch_assoc($result)) {
        $locais[] = [
            "id" => (int)$row['id'], // Converte id para inteiro
            "nome" => $row['nome'],
            "capacidade" => (int)$row['capacidade'] // Converte capacidade para inteiro
        ];
    }

    // Retorna os locais como JSON
    http_response_code(200);
    echo json_encode($locais);
} else {
    http_response_code(500);
    echo json_encode([
        "status" => "erro",
        "mensagem" => "Erro ao buscar locais: " . mysqli_error($conexao)
    ]);
}

mysqli_close($conexao);

?>
