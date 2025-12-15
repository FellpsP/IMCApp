
/** ChatGPT – início
 * Prompt: "Crie um objeto Kotlin chamado HealthCalculator com funções para calcular IMC (Índice de Massa Corporal), classificar IMC, calcular TMB (Taxa Metabólica Basal) usando a fórmula Mifflin-St Jeor, calcular peso ideal usando a fórmula Devine e calcular necessidades calóricas diárias baseadas no nível de atividade física. Inclua validações básicas nos cálculos."
 *
 */
package com.example.imc.domain

/**
 * Objeto que contém funções para cálculo de métricas de saúde.
 *
 * Esta calculadora implementa fórmulas padrão da área de saúde e nutrição
 * para fornecer estimativas de IMC, TMB, peso ideal e necessidades calóricas.
 *
 * @constructor Este objeto não pode ser instanciado - todos os métodos são estáticos.
 */
object HealthCalculator {

    /**
     * Calcula o Índice de Massa Corporal (IMC).
     *
     * O IMC é uma medida que relaciona peso e altura para estimar
     * se uma pessoa está com peso adequado.
     *
     * @param weight Peso em quilogramas (kg). Deve ser maior que zero.
     * @param height Altura em metros (m). Deve ser maior que 0,5 metros.
     * @return O valor do IMC calculado. Retorna 0.0 se os parâmetros forem inválidos.
     * @throws ArithmeticException Se ocorrer erro matemático durante o cálculo.
     */
    fun calculateBMI(weight: Double, height: Double): Double {
        if (weight <= 0.0 || height <= 0.0) return 0.0
        if (height < 0.5) return 0.0  // Altura mínima de 50cm
        return weight / (height * height)
    }

    /**
     * Classifica o valor do IMC em categorias padrão da OMS.
     *
     * As categorias são baseadas nas diretrizes da Organização Mundial da Saúde:
     * - Abaixo do peso: IMC < 18.5
     * - Peso normal: 18.5 ≤ IMC < 25
     * - Sobrepeso: 25 ≤ IMC < 30
     * - Obesidade Grau I: 30 ≤ IMC < 35
     * - Obesidade Grau II: 35 ≤ IMC < 40
     * - Obesidade Grau III: IMC ≥ 40
     *
     * @param bmi Valor do IMC a ser classificado.
     * @return String descritiva da classificação do IMC.
     */
    fun classifyBMI(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Abaixo do peso"
            bmi < 25.0 -> "Peso normal"
            bmi < 30.0 -> "Sobrepeso"
            bmi < 35.0 -> "Obesidade Grau I"
            bmi < 40.0 -> "Obesidade Grau II"
            else -> "Obesidade Grau III"
        }
    }

    /**
     * Calcula a Taxa Metabólica Basal (TMB) usando a fórmula Mifflin-St Jeor.
     *
     * A TMB representa a quantidade de calorias que o corpo queima em repouso
     * para manter funções vitais como respiração e circulação.
     *
     * @param weight Peso em quilogramas (kg).
     * @param height Altura em centímetros (cm).
     * @param age Idade em anos.
     * @param isMale true para homens, false para mulheres.
     * @return Valor da TMB em calorias por dia.
     */
    fun calculateBMR(weight: Double, height: Double, age: Int, isMale: Boolean): Double {
        return if (isMale) {
            (10 * weight) + (6.25 * height) - (5 * age) + 5
        } else {
            (10 * weight) + (6.25 * height) - (5 * age) - 161
        }
    }

    /**
     * Calcula o peso ideal usando a fórmula de Devine.
     *
     * A fórmula de Devine é comumente usada para estimar peso corporal ideal
     * baseado na altura e gênero.
     *
     * @param height Altura em centímetros (cm).
     * @param isMale true para homens, false para mulheres.
     * @return Peso ideal estimado em quilogramas (kg).
     */
    fun calculateIdealWeight(height: Double, isMale: Boolean): Double {
        val heightInInches = height / 2.54  // Converte cm para polegadas
        val baseWeight = if (isMale) 50.0 else 45.5  // Peso base para 5 pés (60 polegadas)

        /** calculateIdealWeight – início
         * razão: Ajustada a fórmula para considerar apenas alturas acima de 60 polegadas
         * Aprendizado: A fórmula original de Devine assume altura mínima de 5 pés
         *
         */
        return if (heightInInches > 60) {
            baseWeight + 2.3 * (heightInInches - 60)
        } else {
            baseWeight
        }
        /** calculateIdealWeight – final */
    }

    /**
     * Calcula as necessidades calóricas diárias baseadas na TMB e nível de atividade.
     *
     * Multiplica a TMB por um fator que corresponde ao nível de atividade física:
     * - Sedentário: pouco ou nenhum exercício (fator 1.2)
     * - Leve: exercício leve 1-3 dias/semana (fator 1.375)
     * - Moderado: exercício moderado 3-5 dias/semana (fator 1.55)
     * - Intenso: exercício intenso 6-7 dias/semana (fator 1.725)
     *
     * @param bmr Taxa Metabólica Basal em calorias/dia.
     * @param activityLevel Nível de atividade física (1 a 4).
     * @return Calorias necessárias diárias para manutenção do peso.
     */
    fun calculateDailyCalorieNeeds(bmr: Double, activityLevel: Int): Double {
        val multiplier = when (activityLevel) {
            1 -> 1.2   // Sedentário
            2 -> 1.375 // Leve
            3 -> 1.55  // Moderado
            4 -> 1.725 // Intenso
            else -> 1.2  // Valor padrão para entrada inválida
        }
        return bmr * multiplier
    }
}
/** ChatGPT – final */