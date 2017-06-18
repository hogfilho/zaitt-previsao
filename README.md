# zaitt-previsao
Repositório do App Previsao de Tempo solicitado como teste pela Zaitt

# Arquitetura
Foi utilizada a arquitetura MVC(Model, View, Controller), onde a Interface Gráfica fica totalmente independente da lógica de negócio.
Descrição dos pacotes br.ufes.inf.hfilho.previsotempo:<BR>

.controller - Camada do controlador

.ui - Camada de interface gráfica

.domain - Camada de domínio/modelo

# Design
As guidelines do Material Design do Google foram seguidas, incorporando um design simplista no estilo bold

# Bibliotecas
Eu poderia ter utilizado alguma biblioteca para acessar o OpenWeatherMap, vi que existem muitas no github, porém preferi não utilizar nenhuma porque adicionar uma biblioteca para utilizar uma ou duas funções apenas ocuparia espaço desnecessáriamente no aplicativo. Digo isso pois gosto muito de programação baixo-nível e tenho muita experiência com Regex(Expressões Regulares), que foi o que utilizei para extrair os dados da api do OpenWeatherMap.

Quanto à biblioteca de persistência de dados, também não utilizei nenhuma. Optei por salvar no SharedPreferences do Android por ser um aplicativo simples.

Utilizei a biblioteca Glide(Recomendada pela google, diferente da Picasso) para manter um cache das imagens para que elas também apareçam em modo offline. 

# Compatibilidade
O código, como solicitado, é compatível com a versão estável mais recente do Android, que é a API 25 versão Nougat 7.1.1.
Foi testado no emulador rodando a imagem do Nougat 7.1.1 e também em um Motorola G (segunda geração), tanto em modo online quanto offline.
