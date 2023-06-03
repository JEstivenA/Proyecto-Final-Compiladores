/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;


/**
 *
 * @author estiv
 */   
public class Calculadora {

    public static void main(String[] args) {
        String archivo = "EntradaConErrores.txt"; // Nombre del archivo de operaciones
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(archivo));
            String linea;
            while ((linea = br.readLine()) != null) {
                if (verificarBalance(linea)) {
                   try {
                        double resultado = evaluarOperacion(linea);
                        System.out.println("Resultado: " + linea + resultado);
                    } catch (ArithmeticException e) {
                        System.out.println("Error: División por cero no está permitida");
                    }
                } else {
                    System.out.println("La expresión contiene paréntesis o corchetes desbalanceados: " + linea);
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    public static boolean verificarBalance(String expresion) {
        Stack<Character> pila = new Stack<>();

        for (int i = 0; i < expresion.length(); i++) {
            char caracter = expresion.charAt(i);
            if (caracter == '(' || caracter == '[') {
                pila.push(caracter);
            } else if (caracter == ')' || caracter == ']') {
                if (pila.isEmpty() || !esParBalanceado(pila.peek(), caracter)) {
                    return false;
                }
                pila.pop();
            }
        }

        return pila.isEmpty();
    }

    public static boolean esParBalanceado(char apertura, char cierre) {
        return (apertura == '(' && cierre == ')') || (apertura == '[' && cierre == ']');
    }

    public static double evaluarOperacion(String operacion) {
        // Convertir la expresión en una lista de tokens
        String[] tokens = obtenerTokens(operacion);
        System.out.println("Operacion desdde metodo evaluar " + operacion);
        // Crear pilas para operandos y operadores
        Stack<Double> pilaOperandos = new Stack<>();
        Stack<Character> pilaOperadores = new Stack<>();

        for (String token : tokens) {
            if (esNumero(token)) {
                double numero = Double.parseDouble(token);
                pilaOperandos.push(numero);
            } else if (esOperador(token)) {
                char operador = token.charAt(0);
                while (!pilaOperadores.isEmpty() && tienePrecedencia(pilaOperadores.peek(), operador)) {
                    double resultado = aplicarOperador(pilaOperadores.pop(), pilaOperandos.pop(), pilaOperandos.pop());
                    pilaOperandos.push(resultado);
                }
                pilaOperadores.push(operador);
            } else if (token.equals("(") || token.equals("[")) {
                pilaOperadores.push(token.charAt(0));
            } else if (token.equals(")") || token.equals("]")) {
                while (!pilaOperadores.isEmpty() && pilaOperadores.peek() != '(' && pilaOperadores.peek() != '[') {
                    double resultado = aplicarOperador(pilaOperadores.pop(), pilaOperandos.pop(), pilaOperandos.pop());
                    pilaOperandos.push(resultado);
                }
                if (!pilaOperadores.isEmpty() && (pilaOperadores.peek() == '(' || pilaOperadores.peek() == '[')) {
                    pilaOperadores.pop(); // Eliminar el paréntesis o corchete de apertura
                } else {
                    throw new IllegalArgumentException("Expresión inválida: paréntesis o corchetes desbalanceados");
                }
            }
        }
//        
//        System.out.println("pilaOperadores " + pilaOperadores); 
//        System.out.println("pilaOperandos " + pilaOperandos); 

        while (!pilaOperadores.isEmpty()) {
            double resultado = aplicarOperador(pilaOperadores.pop(), pilaOperandos.pop(), pilaOperandos.pop());
            //System.out.println("REsultado aplicaoperador " + resultado +  " PilaOperadoores " + pilaOperadores + " Pila Operandos " + pilaOperandos);
            pilaOperandos.push(resultado);
        }

        return pilaOperandos.pop();
    }

    public static String[] obtenerTokens(String expresion) {
        // Agregar espacios alrededor de los operadores y paréntesis
        expresion = expresion.replaceAll("([+\\-*/()\\[\\]=])", " $1 ");
        // Eliminar espacios duplicados
        expresion = expresion.replaceAll("\\s+", " ");
        // Dividir la expresión en tokens
        return expresion.trim().split(" ");
    }

    public static boolean esNumero(String token) {
        try {
            Double.valueOf(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean esOperador(String token) {
        return token.length() == 1 && "+-*/".contains(token);
    }

    public static boolean tienePrecedencia(char operador1, char operador2) {
        if ((operador1 == '*' || operador1 == '/') && (operador2 == '+' || operador2 == '-')) {
            return true;
        }
        return false;
    }

    public static double aplicarOperador(char operador, double operand2, double operand1) {
        switch (operador) {
            case '+':
                return operand1 + operand2;
            case '-':
                return operand1 - operand2;
            case '*':
                return operand1 * operand2;
            case '/':
                if (operand2 == 0) {
                    throw new ArithmeticException("División por cero no está permitida");
                }
                return operand1 / operand2;
            default:
                throw new IllegalArgumentException("Operador inválido: " + operador);
        }
    }
}
