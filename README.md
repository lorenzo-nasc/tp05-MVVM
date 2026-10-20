# 🎬 Filmes Favoritos (Android App)

Um aplicativo Android em Kotlin para gerenciamento de filmes favoritos. O projeto demonstra a aplicação da arquitetura **MVVM (Model-View-ViewModel)**, manipulação de dados em lista com **RecyclerView**, binding de views usando **View Binding** e carregamento dinâmico de imagens através da biblioteca **Coil**.

---

## 🚀 Funcionalidades

- **Visualização de Lista:** Exibição dos filmes cadastrados com título, nota e poster.
- **Adição de Filmes:** Diálogo modal para inserção de novos filmes (nome, nota e URL da imagem).
- **Edição Dinâmica:** Edição dos dados de um filme existente ao clicar sobre o item.
- **Remoção de Itens:** Possibilidade de excluir filmes diretamente da lista.
- **Carregamento de Imagens Assíncrono:** Uso da biblioteca **Coil** para carregar capas de filmes via URL HTTP/HTTPS com *placeholders* visuais para falhas ou carregamento.

---

## 🛠️ Tecnologias e Bibliotecas Utilizadas

- **Linguagem:** [Kotlin](https://kotlinlang.org/)
- **Arquitetura:** MVVM (Model-View-ViewModel)
- **UI & Layout:**
  - `RecyclerView` + `CardView`
  - `View Binding` (Acesso seguro aos elementos do layout)
  - `AlertDialog` customizado para formulários de criação/edição
- **Gerenciamento de Estado:** `LiveData` e `ViewModel` (Android Jetpack)
- **Carregamento de Imagens:** [Coil](https://coil-kt.github.io/coil/) (`io.coil-kt:coil`)

---

## 🏗️ Arquitetura do Projeto (MVVM)

O projeto segue a padronização MVVM para separar as responsabilidades de UI, regra de negócio e dados:

```
br.unisanta.filmesfavoritos/
├── model/
│   └── Movie.kt            # Modelo de dados do filme (id, title, rating, imageUrl)
├── repository/
│   └── MovieRepository.kt  # Gerenciamento da fonte de dados (em memória)
├── viewmodel/
│   └── MovieViewModel.kt   # Lógica de negócios e comunicação com a UI via LiveData
└── ui/
    ├── MainActivity.kt     # Activity principal (View)
    └── MovieAdapter.kt     # Adapter do RecyclerView
```

### Componentes Principais

1. **Model (`Movie.kt`):** Representa a entidade Filme com ID único, título, nota e URL da imagem.
2. **Repository (`MovieRepository.kt`):** Simula a retenção de dados mantendo uma lista mutável e expõe métodos de inserção, alteração e exclusão.
3. **ViewModel (`MovieViewModel.kt`):** Mantém o estado da tela por meio de um `LiveData<List<Movie>>`, garantindo a persistência dos dados diante de mudanças de configuração (como rotação de tela).
4. **View (`MainActivity` / `MovieAdapter`):** Observa as alterações no `LiveData` do ViewModel e atualiza os elementos visuais na tela via **View Binding**.

---

## 📸 Carregamento de Imagens com Coil

O Coil foi integrado ao `MovieAdapter` para realizar o carregamento assíncrono das imagens de forma eficiente:

```kotlin
binding.imgMoviePoster.load(movie.imageUrl) {
    crossfade(true)
    placeholder(R.drawable.ic_movie_placeholder)
    error(R.drawable.ic_movie_placeholder)
}
```

---

## 📋 Pré-requisitos para Execução

- **Android Studio:** Jellyfish / Ladybug (ou superior)
- **JDK:** 17 ou superior
- **Compile SDK:** 34 / 35
- **Min SDK:** 24

---

## 🔧 Como Executar o Projeto

1. Clone ou baixe este repositório:
   ```bash
   git clone https://github.com/lorenzo-nasc/tp05-MVVM
   ```
2. Abra o Android Studio e selecione **Open...** indicando a pasta raiz do projeto.
3. Aguarde o término da sincronização do **Gradle**.
4. Certifique-se de estar conectado à internet (para carregar as imagens via URL do Coil).
5. Selecione um emulador ou dispositivo físico com **Android 7.0 (API 24)** ou superior e clique em **Run (Shift + F10)**.