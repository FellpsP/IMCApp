# Documento de Implementação do IMCApp

## Fórmulas Utilizadas

As seguintes fórmulas foram implementadas na classe `HealthCalculator` para os cálculos de saúde:

- **Índice de Massa Corporal (IMC):**
  `IMC = peso (kg) / (altura (m) * altura (m))`

- **Taxa Metabólica Basal (TMB) - Fórmula de Mifflin-St Jeor:**
  - **Homens:** `TMB = (10 * peso) + (6.25 * altura em cm) - (5 * idade) + 5`
  - **Mulheres:** `TMB = (10 * peso) + (6.25 * altura em cm) - (5 * idade) - 161`

- **Peso Ideal - Fórmula de Robinson:**
  - **Homens:** `50 kg + 2.3 kg para cada polegada acima de 5 pés`
  - **Mulheres:** `45.5 kg + 2.3 kg para cada polegada acima de 5 pés`

- **Estimativa de Necessidade Calórica Diária:**
  `Necessidade Calórica = TMB * Fator de Atividade`
  - **Sedentário:** 1.2
  - **Leve:** 1.375
  - **Moderado:** 1.55
  - **Intenso:** 1.725

## Modelo de Dados e Persistência

A persistência de dados é implementada com a biblioteca **Room**.

- **Banco de Dados:** A classe `AppDatabase` é a implementação do banco de dados Room.
- **Entidade:** A classe `BmiData` representa a tabela `bmi_records` no banco de dados e armazena as seguintes informações para cada medição:
    - `id`: Identificador único (chave primária).
    - `weight`: Peso.
    - `height`: Altura.
    - `bmi`: IMC calculado.
    - `classification`: Classificação do IMC.
    - `timestamp`: Data e hora da medição.
    - `bmr`: Taxa Metabólica Basal.
    - `idealWeight`: Peso ideal.
    - `dailyCalorieNeeds`: Necessidade calórica diária.
    - `age`: Idade do usuário.
    - `isMale`: Sexo do usuário.
    - `activityLevel`: Nível de atividade física.
- **DAO (Data Access Object):** A interface `BmiDao` define os métodos para acessar o banco de dados (inserir, buscar, etc.).
- **Repositório:** Um repositório (`BmiRepository`) é usado para abstrair a fonte de dados, fornecendo uma API limpa para o resto do aplicativo acessar os dados.

## Melhorias Futuras

- **Autenticação de Usuário:** Implementar um sistema de login para que múltiplos usuários possam armazenar seus dados de forma privada.
- **Integração com API Externa:** Conectar o aplicativo a uma API de saúde para obter informações nutricionais de alimentos ou sincronizar dados com outras plataformas de saúde.
- **Visualização Gráfica:** Adicionar gráficos para visualizar a evolução do IMC, peso e outros indicadores ao longo do tempo.
- **Suporte para Mais Idiomas:** Internacionalizar o aplicativo para oferecer suporte a diferentes idiomas.
- **Testes Unitários e de Interface:** Implementar testes para garantir a qualidade e a robustez do código.