<?php

$servidor = 'localhost';
$banco = 'bdeventos';
$usuario = 'root';
$senha = '';

$conexao = mysqli_connect($servidor, $usuario, $senha, $banco);

if (!$conexao) {
    echo json_encode([
        "status" => "erro",
        "mensagem" => "Falha na conexão com o banco de dados: " . mysqli_connect_error()
    ]);
    exit();
}

$query = "SELECT id, nome, capacidade FROM locais";
$result = mysqli_query($conexao, $query);

if ($result) {
    $locais = [];
    while ($row = mysqli_fetch_assoc($result)) {
        $locais[] = [
            "id" => $row['id'],
            "nome" => $row['nome'],
            "capacidade" => intval($row['capacidade'])
        ];
    }

    echo json_encode($locais);
} else {
    echo json_encode([
        "status" => "erro",
        "mensagem" => "Erro ao buscar locais: " . mysqli_error($conexao)
    ]);
}

mysqli_close($conexao);

?>
