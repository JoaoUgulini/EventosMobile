<?php
$servidor = 'localhost';
$banco      = 'bdeventos';
$usuario  = 'root';
$senha    = '';

$json = file_get_contents('php://input');
$obj = json_decode($json);

$texto1=$obj->login;
$texto2=$obj->senha;

$conexao = mysqli_connect($servidor, $usuario, $senha, $banco);
$dados = mysqli_query($conexao, "SELECT nome,senha,id FROM usuario where nome = '".$texto1."' and senha = '".$texto2."' limit 1");
while ($func = mysqli_fetch_assoc($dados)):
    $vetor['usuario'][]=array_map('utf8_encode',$func);
endwhile;
echo json_encode($vetor);

file_put_contents('dados.txt',json_encode($vetor) );

?>
