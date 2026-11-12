import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // Ejemplo: arbol del enunciado
        //            A
        //          /   \
        //         B     C
        //        / \   / \
        //       D   E F   G
        //      / \
        //     H   I
        TreeNode a = new TreeNode("A");
        BinaryTree tree = new BinaryTree(a);

        a.left = new TreeNode("B");
        a.right = new TreeNode("C");
        a.left.left = new TreeNode("D");
        a.left.right = new TreeNode("E");
        a.right.left = new TreeNode("F");
        a.right.right = new TreeNode("G");
        a.left.left.left = new TreeNode("H");
        a.left.left.right = new TreeNode("I");

        // Un solo Scanner para todo el programa (varios Scanner sobre
        // System.in pueden perder datos del buffer).
        Scanner scanner = new Scanner(System.in);
        int option = 0;

        while (option != 8) {
            System.out.println();
            System.out.println("========== MENU ==========");
            System.out.println("1. Construir arbol");
            System.out.println("2. Mostrar arbol");
            System.out.println("3. Recorrido Preorden");
            System.out.println("4. Recorrido Inorden");
            System.out.println("5. Recorrido Posorden");
            System.out.println("6. Mostrar factores de equilibrio");
            System.out.println("7. Balancear arbol (AVL)");
            System.out.println("8. Salir");
            System.out.println("==========================");
            System.out.print("Seleccione una opcion: ");

            if (!scanner.hasNextLine()) {
                break; // fin de la entrada
            }
            String line = scanner.nextLine().trim();
            try {
                option = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                option = 0;
            }
            System.out.println();

            switch (option) {
                case 1:
                    tree.buildTree(scanner);
                    System.out.println();
                    tree.printTree();
                    break;
                case 2:
                    tree.printTree();
                    break;
                case 3:
                    System.out.println("Preorden: " + tree.preorder());
                    break;
                case 4:
                    System.out.println("Inorden: " + tree.inorder());
                    break;
                case 5:
                    System.out.println("Posorden: " + tree.postorder());
                    break;
                case 6:
                    tree.printBalanceFactors();
                    break;
                case 7:
                    tree.balance();
                    break;
                case 8:
                    System.out.println("Hasta luego.");
                    break;
                default:
                    System.out.println("Opcion invalida. Intente de nuevo.");
            }
        }
        scanner.close();
    }
}
