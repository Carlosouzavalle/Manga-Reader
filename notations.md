### @Controller

@Controller avisa ao Spring: “essa classe atende pedidos web, pode criar ela e usar”. Você nunca faz new HomeController(). O Spring faz isso sozinho quando o programa liga.

O que é um controller

É a classe que recebe um pedido do navegador (alguém abriu um endereço) e decide a resposta. Pense no garçom: o cliente pede, ele vai buscar o que precisa e devolve.


=================================================

### @Value

@Value("${manga.root-folder-id}")
private String rootId;

@Value busca o valor no application.properties. O Spring lê manga.root-folder-id de lá e coloca na variável rootId. Assim o ID da pasta fica fora do código.

=================================================

### model

O parâmetro Model model é uma caixa vazia que o Spring te entrega. O que você colocar nela fica disponível para o HTML usar.


=================================================

### Alguns metodos usados

.filter(...) mantém só as pastas. Arquivos soltos são descartados.

.sorted(...) ordena por nome, ignorando maiúsculas e minúsculas.

.map(...) transforma cada pasta em um MangaCard, com o id, o nome e o id da capa (que vem do findCover).

.toList() junta o resultado numa lista.

=====================================================
### Service

@Service avisa ao Spring: “crie um objeto desta classe e deixe disponível para quem precisar”. É isso que permite o controller receber o DriveService pronto no construtor.

O que é um service

É a classe que faz o trabalho de verdade. O controller recebe o pedido e decide o que responder, e o service sabe como buscar os dados. No nosso caso, o DriveService é o único que conversa com o Google Drive.

=====================================================

Um record é uma classe simples só para guardar dados. Esse aqui representa o formato da resposta do Google, que vem assim:

====================================================

O RestClient é o “navegador” do seu programa Java: ele faz pedidos HTTP a outros sites. Aqui já deixamos configurado o endereço base do Google, então depois basta informar o caminho.

=====================================================

O método listfolder

O Google não entrega tudo de uma vez quando a pasta é grande. Ele entrega um bloco e, se ainda tem mais, manda um nextPageToken, que funciona como um marcador de página. O laço faz assim:
=========================================================

> final String token = pageToken;

Truque do Java: uma variável usada dentro de uma lambda (o u -> {...} logo abaixo) não pode mudar de valor. Como o pageToken muda a cada volta do laço, copiamos para uma variável final e usamos a cópia.
=========================================================

http.get() prepara um pedido do tipo GET.
=========================================================

.uri(...) monta o endereço completo. Ali dentro adicionamos o caminho /drive/v3/files e os parâmetros: q (a busca “arquivos que têm esta pasta como pai e não estão na lixeira”), pageSize, fields (quais campos queremos de volta, para a resposta vir leve) e key (a API key).
=========================================================

.retrieve() envia o pedido.
=========================================================

.body(DriveList.class) pega a resposta e transforma no record que definimos.
=========================================================

return http.get()
    .uri(u -> u.path("/drive/v3/files/{id}")
        .queryParam("alt", "media")
        .queryParam("key", apiKey)
        .build(fileId))
    .retrieve()
    .toEntity(byte[].class);

Aqui o pedido é diferente: em vez de listar, ele baixa o conteúdo de um arquivo. O alt=media é o que diz ao Drive “quero o arquivo em si, não os dados sobre ele”. O {id} é preenchido pelo fileId. O .toEntity(byte[].class) devolve a imagem como bytes, junto com os cabeçalhos da resposta, como o tipo (jpeg, png). O ImageController usa esse tipo para avisar ao navegador que está mandando uma imagem.

=========================================================
.toEntity
O método toEntity() em Java é comumente utilizado para converter um objeto DTO (Data Transfer Object) em uma Entity (Entidade de Banco de Dados).
Isso é muito comum em arquiteturas que separam a camada de apresentação/API (onde chegam os dados do cliente) da camada de persistência (JPA/Hibernate)

=========================================================

.queryParam
O método .queryParam() da interface UriBuilder (do Spring Framework) serve para anexar parâmetros de consulta (query string) ao final de uma URL de forma segura e programática.


Principais funções do .queryParam()

• Anexa chaves e valores. O método recebe o nome do parâmetro e seu valor, adicionando automaticamente o ? antes do primeiro parâmetro e o & entre os seguintes.

• Aceita múltiplos valores. Se você passar uma coleção ou vários valores para a mesma chave (ex: .queryParam("tag", "java", "spring")), ele repete o parâmetro na URL (?tag=java&tag=spring).

• Suporta variáveis de template. Você pode usar marcadores como .queryParam("filtro", "{valor}") e preencher o dado real somente na hora de chamar o .build().

• Garante a codificação da URL. Junto com a construção da URI, ele evita erros de sintaxe causados por espaços, acentos ou caracteres especiais nos valores enviados.

• Permite encadeamento fluente. Como retorna a própria instância do UriBuilder, você pode chamar vários .queryParam() em sequência de forma limpa e legível.

===========================================================

O método .build() da interface UriBuilder serve para finalizar a montagem da URL e gerar o objeto URI pronto para uso, substituindo todas as variáveis de espaço reservado (como {id}) pelos valores reais fornecidos.

Principais funções do .build()

• Preenche as variáveis de template: Se você definiu trechos dinâmicos no caminho ou nos parâmetros usando chaves {...}, o .build() recebe os valores e os encaixa na ordem exata em que aparecem.
• Executa a codificação final (URL Encoding): Ele converte espaços, barras, acentos e símbolos especiais dos seus valores para o formato válido da web (por exemplo, transformando um espaço em %20), garantindo que a URL não quebre.
• Retorna um objeto java.net.URI: Ele encerra o modo de edição do construtor e entrega o objeto imutável que o cliente HTTP (RestClient ou WebClient) precisa para disparar a requisição na rede.


==============================================================

O método .filter() em Java faz parte da Stream API (introduzida no Java 8) e serve para filtrar elementos de uma coleção (como uma List ou Set) com base em uma condição lógica.
=========================================================

O método .map() da Java Streams API serve para transformar cada elemento de uma stream em outro objeto, valor ou tipo.

Características Principais

• Não altera a coleção original: A lista ou fonte de dados permanece intacta; o map gera uma nova sequência de dados transformados.
• Operação intermediária: Ela é "preguiçosa" (lazy), o que significa que a transformação só é executada quando uma operação terminal (como .collect() ou .forEach()) é acionada.
• Mudança de tipo: Pode converter um tipo de dado em completamente outro (por exemplo, Stream<String> em Stream<Integer>)
=========================================================

O método findFirst() faz parte da Stream API do Java (introduzida no Java 8) e serve para retornar o primeiro elemento de um fluxo (Stream)

Características principais

• Retorno: Devolve um objeto do tipo Optional<T>, evitando erros de NullPointerException caso o fluxo esteja vazio.
• Operação terminal: É uma operação terminal e short-circuiting (de curto-circuito), o que significa que ela encerra a execução do Stream assim que encontra o primeiro elemento, sem precisar processar o restante da coleção.
• Ordem: Respeita a ordem de encontro (encounter order) dos elementos da fonte.
=========================================================

O método orElse() da classe Optional em Java serve para retornar o valor contido no Optional se ele estiver presente, ou um valor padrão (fallback) caso o Optional esteja vazio.

A grande diferença: orElse vs orElseGet

orElse(T other): O argumento passado dentro do orElse é executado/avaliado sempre, mesmo que o Optional contenha um valor. Se você passar uma chamada de método como orElse(buscarNoBancoDeDados()), esse método será executado toda vez, independentemente de o Optional estar preenchido ou não.

orElseGet(Supplier<? extends T> other): O argumento é uma Supplier (execução lazy / sob demanda). O método ou cálculo só é executado se o Optional realmente estiver vazio.

=========================================================

sorted()

Na Stream API do Java, o método sorted() é uma operação intermediária com estado (stateful) que retorna um novo fluxo contendo os elementos ordenados. Como destacam desenvolvedores em discussões no Stack Overflow, ele não altera a lista ou coleção original, exigindo que o resultado seja coletado ao final.

1. Ordenação Natural 

• Requisito obrigatório. Os elementos do fluxo devem implementar a interface Comparable. Caso contrário, o programa lançará uma exceção ClassCastException em tempo de execução.

• Uso comum. Ideal para listas simples de textos, números ou datas.

List<String> frutas = List.of("Uva", "Banana", "Maçã");

List<String> ordenadas = frutas.stream()
    .sorted()
    .toList(); // Resultado: [Banana, Maçã, Uva]



2. Ordenação Customizada (sorted(Comparator))

Esta versão recebe um Comparator como argumento para definir regras personalizadas de ordenação, sendo essencial para objetos complexos.

• Ordem reversa. Você pode usar Comparator.reverseOrder() para inverter a ordenação natural de tipos simples.

• Extração de atributos. Utilize Comparator.comparing() combinado com referências de método para ordenar objetos por um campo específico.

• Critérios de desempate. Encadeie o método .thenComparing() para definir uma segunda ou terceira regra caso o primeiro campo seja igual.

• Tratamento de nulos. Envolva seu comparador com Comparator.nullsFirst() ou Comparator.nullsLast() para evitar erros de NullPointerException durante a ordenação.

// Exemplo combinando múltiplos critérios e ordem reversa
List<Produto> produtosOrdenados = produtos.stream()
    .sorted(Comparator.comparing(Produto::getCategoria)
        .thenComparing(Comparator.comparing(Produto::getPreco).reversed()))
    .toList();

=========================================================================

O método charAt() da classe String em Java retorna o caractere localizado em um índice (posição) específico da string.

O método Character.isDigit() em Java é um método estático que serve para verificar se um caractere específico é um dígito (um número de '0' a '9' ou dígitos equivalentes em outros alfabetos Unicode)

O método substring() da classe String em Java serve para extrair uma parte (uma sequência de caracteres) de uma string existente.

O método compareTo() em Java é utilizado para comparar dois elementos e definir uma ordem lexicográfica (alfabética ou baseada em valores Unicode) ou numérica. Ele faz parte da interface Comparable e é muito usado para ordenação de listas e arrays.