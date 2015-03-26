# Оригинальный набор команд [БЭВМ](bcomp.md) #



# Набор команд #
| **Наименование** | **Мнемоника** | **Код** | **Описание** |
|:-----------------------------|:-----------------------|:-----------|:---------------------|
| **Адресные команды** | | | |
| Приращение и пропуск | ISZ М | 0XXX | (М) + 1 → М; Если (М) ≥ 0, то (СК) + 1 → СК |
| Логическое умножение | AND М | 1XXX | (A) & (М) → A |
| Обращение к подпрограмме | JSR М | 2XXX | (СК) → М; М + 1 → СК |
| Пересылка | MOV М | 3XXX | (A) → М |
| Сложение | ADD М | 4XXX | (A) + (М) → A |
| Сложение с переносом | ADC М | 5XXX | (A) + (С) + (М) → A |
| Вычитание | SUB М | 6XXX | (A) - (М) → A |
| Переход, если перенос | BCS М | 8XXX | Если (С) = 1, то М → СК |
| Переход, если плюс | BPL М | 9XXX | Если (А) ≥ 0, то М → СК |
| Переход, если минус | BMI М | AXXX | Если (А) < 0, то М → СК |
| Переход, если нуль | BEQ М | BXXX | Если (А) = 0, то М → СК |
| Безусловный переход | BR М | CXXX | М → СК |
| **Безадресные команды** | | | |
| Останов | HLT | F000 |  |
| Нет операции | NOP | F100 |  |
| Очистка аккумулятора | CLA | F200 | 0 → А |
| Очистка флага переноса | CLC | F300 | 0 → С |
| Инверсия аккумулятора | CMA | F400 | (!А) → А |
| Инверсия флага переноса | CMC | F500 | (!С) → С |
| Циклический сдвиг влево | ROL | F600 | Содержимое А и С сдвигается влево |
| Циклический сдвиг вправо | ROR | F700 | Содержимое А и С сдвигается вправо |
| Инкремент аккумулятора | INC | F800 | (А) + 1 → А |
| Декремент аккумулятора | DEC | F900 | (А) - 1 → А |
| Разрешение прерываний | EI | FA00 |  |
| Запрещение прерываний | DI | FB00 |  |
| **Команды ввода-вывода** | | | |
| Очистка флага ВУ | CLF ВУ | E0XX |  |
| Опрос флага ВУ | TSF ВУ | E1XX | Если флаг ВУ = 1, то (СК) + 1 → СК |
| Ввод | IN ВУ | E2XX | (ВУ) → А |
| Вывод | OUT ВУ | E3XX | (А) → ВУ |

# Реализация #
Оригинальный набор команд реализован в:
  1. [Исходной микропрограмме](BaseMicroProgram.md)
  1. [Оптимизированной микропрограмме](OptimizedMicroProgram.md)