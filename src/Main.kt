import kotlinx.coroutines.*

/**
 * В главной функции программа запускается с приветствия: "Программа работы с базой данных сотрудников" и через секундную
 * задержку предлагает выполнить порядок действий: "Добавить сотрудника:\n1.Да\n2.Нет". В цикле можно добавлять бесконечное
 * количество сотрудников, вводя данные имени и зарплаты с консоли и используя функцию addPerson(person: Person). После
 * добавления каждого сотрудника, предлагать продолжение работы или выход с чтением базы данных: "Добавить сотрудника:
 * \n1.Да\n2.Прочитать базу данных"
 */
suspend fun main() = coroutineScope {
    /**
     * Создать список personList типа Person для их хранения.
     */
    val personList: MutableList<Person> = mutableListOf()

    /**
     * Создать Map<Person, Int> resultList.
     */
    val resultList: MutableMap<Person, Int> = mutableMapOf()
    println("Программа работы с базой данных сотрудников")
    delay(1000L)

    println(
        "Добавить сотрудника?\n" +
                "1. Да\n" +
                "2. Нет"
    )
    if (readln() == "1") {
        while (true) {
            PersonManager().addPerson(personList)
            println(
                "Добавить сотрудника?:\n" +
                        "1.Да\n" +
                        "2.Прочитать базу данных"
            )
            if (readln() == "2") {
                println("База данных сотрудников")
                PersonManager().printPersonList(personList)
                break
            }
        }
    } else

        return@coroutineScope
    delay(500L)
    /**
     *  В случае прекращения работы в функции main запускается корутина, которая внутри выполняет функции добавления
     *  пароля сотруднику и вывода в консоль получившегося в результате map. Эти функции описаны были выше.
     */
    val jobFirst = launch {
        for (person in personList) {
            addPassword(person, resultList)
        }
        readDataPersonList(resultList)
    }

    /**
     *    Написать еще одну корутину, которая будет выполняться параллельно с той. В ней указано условие отмены работы
     *    предыдущей корутины, это ввод с консоли значения равного «0». Т.е. при вводе «0» добавление пароля и вывод
     *    данных прекращаются и выходит сообщение "Завершение полной работы".
     */
    val jobSecond = launch(Dispatchers.Default) {
        println("\n\nВведите \"0\" для завершения работы\n\n")
        if (readln() == "0") {
            println("Завершение полной работы")
            jobFirst.cancel()
        }
    }
    jobFirst.join()
    jobSecond.join()
}

/**
 * Написать класс Person с полями имени и зарплаты.
 */
data class Person(val name: String, val salary: Int) {
    override fun toString(): String {
        return "\n----------------------------------------------------------\n" +
                "Имя сотрудника: $name, Зарплата сотрудника: $salary"
    }
}

/**
 * Написать класс PersonManager, в котором будут функция добавления Person в список addPerson(person: Person).
 */
class PersonManager {
    fun printPersonList(inList: MutableList<Person>) {
        if (inList.isNotEmpty()) {
            println(inList.toString())
        } else {
            println("База данных пуста!")
        }
    }

    fun addPerson(inList: MutableList<Person>) {
        println("Введите Имя Сотрудника:")
        val namePerson = readln()
        println("Введите зарплату сотрудника:")
        val salaryPerson = readln().toInt()
        inList += (Person(namePerson, salaryPerson))
    }
}

/**
 * Написать функцию addPassword(), которая будет добавлять в созданный словарь Person и добавлять шестизначный
 * пароль, сгенерированный в этой функции в качестве значения. Эта функция должна добавлять данные элементы с
 * задержкой в 500L для имитации создания надежного пароля.
 */
suspend fun addPassword(person: Person, inMap: MutableMap<Person, Int>) {
    val pass = (100000..999999).random()
    val passToString = pass.toString()
    println("Генерация пароля для ${person.name}\n")
    for (i in passToString.indices) {
        print(passToString[i])
        delay(500L)
    }
    println("\nСгенерирован пароль: $pass")
    inMap[person] = pass
}

/**
 * Написать функцию readDataPersonList(), выводящую данные созданного map в виде ("Сотрудник: ${i.key}; пароль:
 * ${i.value}" с временной задержкой в 1000L.
 */
suspend fun readDataPersonList(inMap: MutableMap<Person, Int>) {
    if (inMap.isNotEmpty()) {
        for (i in inMap) {
            println("Сотрудник: ${i.key}; пароль: ${i.value}")
            delay(1000L)
        }
    } else {
        println("Коллекция пуста!")
    }
}
