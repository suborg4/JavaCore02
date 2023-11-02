package javacorehw02;

// 1 переработал метод проверки checkWin
// 2 Доработан ИИ блокировать ходы игрока aiTurn


import java.util.Random;
import java.util.Scanner;

public class Program {

    private static final char DOT_HUMAN = 'X'; // Фишка игрока - человека
    private static final char DOT_AI = '0'; // Фишка игрока - компьютер
    private static final char DOT_EMPTY = '*'; // Признак пустого поля
    private static final Scanner scanner = new Scanner(System.in);
    private static  final Random random = new Random();
    private static char[][] field; // Двумерный массив хранит состояние игрового поля
    private static int fieldSizeX; // Размерность игрового поля
    private static int fieldSizeY; // Размерность игрового поля
    private static final int WIN_COUNT = 4; // Кол-во фишек для победы

    public static void main(String[] args) {
        while (true){
            initialize();
            printField();
            while (true){
                humanTurn();
                printField();
                if (gameCheck(DOT_HUMAN, "Вы победили!"))
                    break;
                aiTurn();
                printField();
                if (gameCheck(DOT_AI, "Победил компьютер!"))
                    break;
            }
            System.out.print("Желаете сыграть еще раз? (Y - да): ");
            if (!scanner.next().equalsIgnoreCase("Y"))
                break;
        }
    }

    /**
     * Инициализация начального состояния игры
     */
    private static void initialize(){
        fieldSizeX = 5;
        fieldSizeY = 5;
        field = new char[fieldSizeY][fieldSizeX];
        for (int y = 0; y < fieldSizeY; y++){
            for (int x = 0; x < fieldSizeX; x++){
                field[y][x] = DOT_EMPTY;
            }
        }
    }

    /**
     * Отрисовать текущее состояние игрового поля
     */
    private static void printField(){
        System.out.print("+");
        for (int i = 0; i < fieldSizeX*2 + 1; i++){
            System.out.print((i % 2 == 0) ? "-" : i / 2 + 1);
        }
        System.out.println();

        for (int i = 0; i < fieldSizeY; i++){
            System.out.print(i + 1 + "|");
            for (int j = 0; j < fieldSizeX; j++){
                System.out.print(field[i][j] + "|");
            }
            System.out.println();
        }

        for (int i = 0; i < fieldSizeX*2 + 2; i++){
            System.out.print("-");
        }
        System.out.println();
    }

    /**
     * Обработка хода игрока (человека)
     */
    private static void humanTurn(){
        int x, y;
        do{
            System.out.print("Укажите координаты хода X и Y (от 1 до 3)\nчерез пробел: ");
            x = scanner.nextInt() - 1;
            y = scanner.nextInt() - 1;
        }
        while (!isCellValid(x, y) || !isCellEmpty(x, y));
        field[x][y] = DOT_HUMAN;
    }

    /**
     * Обработка хода компьютера с блокировкой ходов игрока
     */
    static void aiTurn() {
        int x, y;

        // Проверяем, есть ли возможность выиграть следующим ходом
        for (x = 0; x < fieldSizeX; x++) {
            for (y = 0; y < fieldSizeY; y++) {
                if (isCellEmpty(x, y)) {
                    field[x][y] = DOT_HUMAN;
                    if (checkWin(DOT_HUMAN)) {
                        field[x][y] = DOT_AI;
                        return;
                    }
                    field[x][y] = DOT_EMPTY;
                }
            }
        }

        // Проверяем, есть ли возможность заблокировать выигрыш игрока следующим ходом
        for (x = 0; x < fieldSizeX; x++) {
            for (y = 0; y < fieldSizeY; y++) {
                if (isCellEmpty(x, y)) {
                    field[x][y] = DOT_HUMAN;
                    if (checkWin(DOT_HUMAN)) {
                        field[x][y] = DOT_AI;
                        return;
                    }
                    field[x][y] = DOT_EMPTY;
                }
            }
        }

        // В противном случае делаем случайный ход
        do {
            x = random.nextInt(fieldSizeX);
            y = random.nextInt(fieldSizeY);
        } while (!isCellEmpty(x, y));

        field[x][y] = DOT_AI;
    }

    /**
     * Проверка, ячейка является пустой (DOT_EMPTY)
     * @param x
     * @param y
     * @return
     */
    static boolean isCellEmpty(int x, int y){
        return field[x][y] == DOT_EMPTY;
    }

    /**
     * Проверка состояния игры
     * @param dot фишка игрока
     * @param winStr победный слоган
     * @return признак продолжения игры (true - завершение игры)
     */
    static boolean gameCheck(char dot, String winStr){
        if (checkWin(dot)){
            System.out.println(winStr);
            return true;
        }
        if (checkDraw()){
            System.out.println("Ничья!");
            return true;
        }
        return false; // Продолжим игру
    }

    /**
     * Проверка корректности ввода
     * @param x
     * @param y
     * @return
     */
    static boolean isCellValid(int x, int y){
        return x >= 0 && x < fieldSizeX && y >= 0 && y < fieldSizeY;
    }

    /**
     * Проверка победы
     * @param c фишка игрока (X или 0)
     * @return
     */
    /**
     * Проверка победы
     * @param c фишка игрока (X или 0)
     * @return true, если есть выигрыш
     */
    static boolean checkWin(char c) {
        int[][] directions = {
                {0, 1}, {1, 0}, {1, 1}, {1, -1}
        };

        for (int i = 0; i < fieldSizeY; i++) {
            for (int j = 0; j < fieldSizeX; j++) {
                if (field[i][j] == c) {
                    for (int[] direction : directions) {
                        int dx = direction[0];
                        int dy = direction[1];

                        if (checkDirection(i, j, dx, dy, c)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    static boolean checkDirection(int x, int y, int dx, int dy, char c) {
        for (int step = 0; step < WIN_COUNT; step++) {
            int newX = x + step * dx;
            int newY = y + step * dy;

            if (!isCellValid(newX, newY) || field[newX][newY] != c) {
                return false;
            }
        }
        return true;
    }

    static boolean check1(int x, int y, char dot, int win){
        return true;
    }

    /**
     * Проверка на ничью
     * @return
     */
    static boolean checkDraw(){
        for (int i = 0; i < fieldSizeY; i++){
            for (int j = 0; j < fieldSizeX; j++){
                if (isCellEmpty(i, j)) return false;
            }
        }
        return true;
    }

}
