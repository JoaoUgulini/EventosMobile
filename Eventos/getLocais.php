<?php

$servidor = 'localhost';
$banco    = 'bdeventos';
$usuario  = 'root';
$senha    = '';

try {
    $conn = new PDO("mysql:host=$servidor;dbname=$banco", $usuario, $senha);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    $stmt = $conn->prepare("SELECT nome_local AS nome, capacidade FROM locais");
    $stmt->execute();

    $locais = $stmt->fetchAll(PDO::FETCH_ASSOC);
    echo json_encode($locais);

} catch (PDOException $e) {
    echo json_encode(['error' => $e->getMessage()]);
}
?>