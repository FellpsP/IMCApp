# IMCApp
O IMCApp é um aplicativo Android para cálculo de IMC e outros indicadores de saúde, com armazenamento de histórico e uma interface moderna construída com Jetpack Compose.

## Funcionalidades

- **Cálculo de IMC:** Calcula o Índice de Massa Corporal (IMC) com base no peso e altura fornecidos.
- **Classificação de IMC:** Apresenta a classificação do IMC (Abaixo do peso, Peso normal, Sobrepeso, etc.).
- **Cálculos Adicionais:**
    - **Taxa Metabólica Basal (TMB):** Calculada com a fórmula de Mifflin-St Jeor.
    - **Peso Ideal:** Calculado com a fórmula de Robinson.
    - **Necessidade Calórica Diária:** Estimada com base na TMB e no nível de atividade física.
- **Histórico de Medições:** Armazena o histórico de todas as medições em um banco de dados local.
- **Detalhes da Medição:** Permite visualizar os detalhes de cada medição do histórico.

## Telas

- **Tela Principal:** Para entrada de dados (peso, altura, idade, sexo, nível de atividade) e cálculo dos indicadores de saúde.
- **Tela de Histórico:** Lista o histórico de medições, ordenado por data.
- **Tela de Detalhes:** Exibe todos os indicadores calculados para um registro de medição específico.

## Arquitetura

O aplicativo segue a arquitetura **MVVM (Model-View-ViewModel)**, com uma clara separação de responsabilidades:

- **View (Composables):** A interface do usuário é construída com Jetpack Compose, seguindo as melhores práticas de gerenciamento de estado.
- **ViewModel:** Atua como intermediário entre a View e o Model, contendo a lógica de apresentação e gerenciando o estado da UI.
- **Model:** Inclui:
    - **Camada de Domínio:** Com a classe `HealthCalculator` que encapsula toda a lógica de negócio e os cálculos de saúde.
    - **Camada de Dados:** Utiliza um repositório para abstrair a fonte de dados (banco de dados local) e o **Room** para persistência de dados.

## Estrutura do Projeto

O código-fonte está organizado nos seguintes pacotes:

- `data`: Contém as classes relacionadas à persistência de dados (Room, DAO, repositório e modelo de dados).
- `domain`: Contém a lógica de negócio do aplicativo (`HealthCalculator`).
- `ui`: Contém os Composables do Jetpack Compose, organizados por tela.
- `MainActivity.kt`: O ponto de entrada do aplicativo.

# Créditos
Fellipe Pietro Rodrigues Gonçalves
Carlos Eduardo Moreno Nunes Guerra
