import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.function.Function;

public class BinaryTree {

    TreeNode root;

    public BinaryTree() {
        this.root = null;
    }

    public BinaryTree(TreeNode root) {
        this.root = root;
    }

    // ======================================================================
    // 1. Construccion del arbol
    // ======================================================================

    /** Si el arbol esta vacio, crea la raiz con 'data'. */
    public void insertRoot(Object data) {
        if (root == null) {
            root = new TreeNode(data);
        }
    }

    /** Devuelve el nodo cuyo dato es equals a 'data', o null si no existe. */
    public TreeNode search(Object data) {
        return searchRec(root, data);
    }

    private TreeNode searchRec(TreeNode node, Object data) {
        if (node == null) {
            return null;
        }
        if (node.data.equals(data)) {
            return node;
        }
        TreeNode found = searchRec(node.left, data);
        if (found != null) {
            return found;
        }
        return searchRec(node.right, data);
    }

    /** Agrega 'data' como hijo izquierdo de 'parent'. false si no se puede. */
    public boolean insertLeft(Object parent, Object data) {
        TreeNode p = search(parent);
        if (p == null || p.left != null) {
            return false;
        }
        p.left = new TreeNode(data);
        return true;
    }

    /** Agrega 'data' como hijo derecho de 'parent'. false si no se puede. */
    public boolean insertRight(Object parent, Object data) {
        TreeNode p = search(parent);
        if (p == null || p.right != null) {
            return false;
        }
        p.right = new TreeNode(data);
        return true;
    }

    /** Lee una linea; si la entrada se termino devuelve "" (hijo inexistente). */
    private String readLine(Scanner scanner) {
        if (!scanner.hasNextLine()) {
            return "";
        }
        return scanner.nextLine().trim();
    }

    /**
     * Construye el arbol leyendo por consola: primero la raiz y luego, por
     * niveles, el hijo izquierdo y derecho de cada nodo. Linea vacia o "-"
     * significa que el hijo no existe.
     */
    public void buildTree(Scanner scanner) {
        root = null;

        System.out.print("Raiz: ");
        String text = readLine(scanner);
        if (text.isEmpty() || text.equals("-")) {
            System.out.println("No se ingreso raiz: el arbol queda vacio.");
            return;
        }
        insertRoot(text);

        // Recorrido por niveles (BFS): se asocia cada hijo directamente al
        // nodo que se esta procesando (asi funciona aunque haya datos repetidos).
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            TreeNode current = queue.poll();

            System.out.print("Hijo izquierdo de " + current.data + ": ");
            String l = readLine(scanner);
            if (!l.isEmpty() && !l.equals("-")) {
                current.left = new TreeNode(l);
                queue.add(current.left);
            }

            System.out.print("Hijo derecho de " + current.data + ": ");
            String r = readLine(scanner);
            if (!r.isEmpty() && !r.equals("-")) {
                current.right = new TreeNode(r);
                queue.add(current.right);
            }
        }
    }

    // ======================================================================
    // 2. Visualizacion
    // ======================================================================

    /** Muestra el arbol en consola de forma grafica. */
    public void printTree() {
        if (root == null) {
            System.out.println("El arbol esta vacio.");
            return;
        }
        System.out.println(draw(root, n -> String.valueOf(n.data)));
    }

    /** Bloque de texto que representa un subarbol ya dibujado. */
    private static class Block {
        String[] lines;
        int width;
        int center; // columna del centro de la etiqueta de la raiz del subarbol

        Block(String[] lines, int width, int center) {
            this.lines = lines;
            this.width = width;
            this.center = center;
        }
    }

    private String draw(TreeNode node, Function<TreeNode, String> labeler) {
        Block b = render(node, labeler);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.lines.length; i++) {
            sb.append(b.lines[i].replaceAll("\\s+$", ""));
            if (i < b.lines.length - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Dibuja recursivamente un subarbol: dibuja los hijos, los coloca lado a
     * lado con la separacion minima necesaria y los une al padre con '/' y '\'.
     */
    private Block render(TreeNode node, Function<TreeNode, String> labeler) {
        Block lb = node.left == null ? null : render(node.left, labeler);
        Block rb = node.right == null ? null : render(node.right, labeler);

        String label = labeler.apply(node);
        int len = label.length();
        int labelCenter = len / 2;

        if (lb == null && rb == null) {
            return new Block(new String[]{label}, len, labelCenter);
        }

        int d;          // numero de filas de conexion '/' '\'
        int lStart = 0; // columna donde empieza el bloque izquierdo
        int rStart = 0; // columna donde empieza el bloque derecho
        int p;          // columna del centro del padre

        if (lb != null && rb != null) {
            d = Math.max(1, Math.floorDiv(rb.center + lb.width - lb.center, 2));
            p = lb.center + d + 1;
            rStart = p + d + 1 - rb.center;
        } else if (lb != null) {
            d = 1;
            p = lb.center + d + 1;
        } else {
            d = 1;
            p = rb.center - d - 1;
        }

        int labelStart = p - labelCenter;
        int minX = labelStart;
        if (lb != null) minX = Math.min(minX, lStart);
        if (rb != null) minX = Math.min(minX, rStart);
        int shift = -minX;

        int maxX = labelStart + len;
        if (lb != null) maxX = Math.max(maxX, lStart + lb.width);
        if (rb != null) maxX = Math.max(maxX, rStart + rb.width);
        int width = maxX + shift;

        int childRows = Math.max(lb == null ? 0 : lb.lines.length,
                                 rb == null ? 0 : rb.lines.length);
        int height = 1 + d + childRows;

        char[][] grid = new char[height][width];
        for (char[] row : grid) {
            java.util.Arrays.fill(row, ' ');
        }

        put(grid, 0, labelStart + shift, label);
        for (int i = 1; i <= d; i++) {
            if (lb != null) grid[i][p - i + shift] = '/';
            if (rb != null) grid[i][p + i + shift] = '\\';
        }
        if (lb != null) {
            for (int j = 0; j < lb.lines.length; j++) {
                put(grid, d + 1 + j, lStart + shift, lb.lines[j]);
            }
        }
        if (rb != null) {
            for (int j = 0; j < rb.lines.length; j++) {
                put(grid, d + 1 + j, rStart + shift, rb.lines[j]);
            }
        }

        String[] lines = new String[height];
        for (int i = 0; i < height; i++) {
            lines[i] = new String(grid[i]);
        }
        return new Block(lines, width, p + shift);
    }

    private void put(char[][] grid, int row, int col, String text) {
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) != ' ') {
                grid[row][col + i] = text.charAt(i);
            }
        }
    }

    // ======================================================================
    // 3. Recorridos (cada uno es una funcion recursiva independiente)
    // ======================================================================

    public String preorder() {
        List<String> out = new ArrayList<>();
        preorderRec(root, out);
        return String.join(" ", out);
    }

    private void preorderRec(TreeNode node, List<String> out) {
        if (node == null) {
            return;
        }
        out.add(String.valueOf(node.data));
        preorderRec(node.left, out);
        preorderRec(node.right, out);
    }

    public String inorder() {
        List<String> out = new ArrayList<>();
        inorderRec(root, out);
        return String.join(" ", out);
    }

    private void inorderRec(TreeNode node, List<String> out) {
        if (node == null) {
            return;
        }
        inorderRec(node.left, out);
        out.add(String.valueOf(node.data));
        inorderRec(node.right, out);
    }

    public String postorder() {
        List<String> out = new ArrayList<>();
        postorderRec(root, out);
        return String.join(" ", out);
    }

    private void postorderRec(TreeNode node, List<String> out) {
        if (node == null) {
            return;
        }
        postorderRec(node.left, out);
        postorderRec(node.right, out);
        out.add(String.valueOf(node.data));
    }

    // ======================================================================
    // 4. Factores de equilibrio
    // ======================================================================

    /** Altura del subarbol: null = 0, hoja = 1. */
    public int height(TreeNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(height(node.left), height(node.right));
    }

    /** Factor de equilibrio = altura(derecho) - altura(izquierdo). */
    public int balanceFactor(TreeNode node) {
        if (node == null) {
            return 0;
        }
        return height(node.right) - height(node.left);
    }

    /** true si todos los nodos tienen factor de equilibrio -1, 0 o 1. */
    public boolean isBalanced() {
        return isBalancedRec(root);
    }

    private boolean isBalancedRec(TreeNode node) {
        if (node == null) {
            return true;
        }
        if (Math.abs(balanceFactor(node)) > 1) {
            return false;
        }
        return isBalancedRec(node.left) && isBalancedRec(node.right);
    }

    /** Datos de los nodos con factor de equilibrio < -1 o > 1. */
    public List<Object> unbalancedNodes() {
        List<Object> result = new ArrayList<>();
        unbalancedRec(root, result);
        return result;
    }

    private void unbalancedRec(TreeNode node, List<Object> result) {
        if (node == null) {
            return;
        }
        unbalancedRec(node.left, result);
        if (Math.abs(balanceFactor(node)) > 1) {
            result.add(node.data);
        }
        unbalancedRec(node.right, result);
    }

    /** Muestra el arbol indicando en cada nodo su factor de equilibrio. */
    public void printBalanceFactors() {
        if (root == null) {
            System.out.println("El arbol esta vacio.");
            return;
        }
        System.out.println("(FE = altura(derecho) - altura(izquierdo); se muestra como Dato[FE])");
        System.out.println(draw(root, n -> n.data + "[" + balanceFactor(n) + "]"));
    }

    // ======================================================================
    // 5. Balanceo AVL
    // ======================================================================

    /** Rotacion simple a la derecha (caso izquierda-izquierda). */
    public TreeNode rotateRight(TreeNode node) {
        if (node == null || node.left == null) {
            return node;
        }
        TreeNode newRoot = node.left;
        node.left = newRoot.right;
        newRoot.right = node;
        return newRoot;
    }

    /** Rotacion simple a la izquierda (caso derecha-derecha). */
    public TreeNode rotateLeft(TreeNode node) {
        if (node == null || node.right == null) {
            return node;
        }
        TreeNode newRoot = node.right;
        node.right = newRoot.left;
        newRoot.left = node;
        return newRoot;
    }

    /** Rotacion doble izquierda-derecha. */
    public TreeNode rotateLeftRight(TreeNode node) {
        if (node == null) {
            return null;
        }
        node.left = rotateLeft(node.left);
        return rotateRight(node);
    }

    /** Rotacion doble derecha-izquierda. */
    public TreeNode rotateRightLeft(TreeNode node) {
        if (node == null) {
            return null;
        }
        node.right = rotateRight(node.right);
        return rotateLeft(node);
    }

    /**
     * Balancea el arbol (AVL): muestra el arbol con sus factores de
     * equilibrio, identifica los nodos desbalanceados, aplica las rotaciones
     * y vuelve a mostrar el arbol.
     */
    public void balance() {
        if (root == null) {
            System.out.println("El arbol esta vacio.");
            return;
        }

        System.out.println("--- 1. Arbol original (con factores de equilibrio) ---");
        printBalanceFactors();

        System.out.println();
        System.out.println("--- 2. Nodos que requieren balanceo ---");
        List<Object> bad = unbalancedNodes();
        if (bad.isEmpty()) {
            System.out.println("Ninguno: el arbol ya es AVL.");
            System.out.println();
            System.out.println("--- 3. Rotaciones aplicadas ---");
            System.out.println("Ninguna.");
            System.out.println();
            System.out.println("--- 4. Arbol despues del balanceo (con factores de equilibrio) ---");
            printBalanceFactors();
            return;
        }
        System.out.println(bad);

        System.out.println();
        System.out.println("--- 3. Rotaciones aplicadas ---");
        List<String> log = new ArrayList<>();
        int passes = 0;
        int maxPasses = 1000; // proteccion
        while (!isBalanced() && passes < maxPasses) {
            root = rebalance(root, log);
            passes++;
        }
        if (!isBalanced()) {
            // Caso extremo (no deberia ocurrir): reconstruir desde el inorden.
            List<Object> values = new ArrayList<>();
            collectInorder(root, values);
            root = buildBalanced(values, 0, values.size() - 1);
            log.add("Reconstruccion equilibrada a partir del recorrido inorden");
        }
        for (String step : log) {
            System.out.println("- " + step);
        }

        System.out.println();
        System.out.println("--- 4. Arbol balanceado (con factores de equilibrio) ---");
        printBalanceFactors();
    }

    /** Pasada posorden: balancea primero los hijos y luego el nodo actual. */
    private TreeNode rebalance(TreeNode node, List<String> log) {
        if (node == null) {
            return null;
        }
        node.left = rebalance(node.left, log);
        node.right = rebalance(node.right, log);

        int bf = balanceFactor(node);
        String name = String.valueOf(node.data);

        if (bf < -1) { // pesado a la izquierda
            if (balanceFactor(node.left) > 0) {
                log.add("Rotacion doble izquierda-derecha en el nodo " + name);
                return rotateLeftRight(node);
            }
            log.add("Rotacion simple a la derecha en el nodo " + name);
            return rotateRight(node);
        }
        if (bf > 1) { // pesado a la derecha
            if (balanceFactor(node.right) < 0) {
                log.add("Rotacion doble derecha-izquierda en el nodo " + name);
                return rotateRightLeft(node);
            }
            log.add("Rotacion simple a la izquierda en el nodo " + name);
            return rotateLeft(node);
        }
        return node;
    }

    private void collectInorder(TreeNode node, List<Object> out) {
        if (node == null) {
            return;
        }
        collectInorder(node.left, out);
        out.add(node.data);
        collectInorder(node.right, out);
    }

    private TreeNode buildBalanced(List<Object> values, int lo, int hi) {
        if (lo > hi) {
            return null;
        }
        int mid = (lo + hi) / 2;
        TreeNode node = new TreeNode(values.get(mid));
        node.left = buildBalanced(values, lo, mid - 1);
        node.right = buildBalanced(values, mid + 1, hi);
        return node;
    }
}
