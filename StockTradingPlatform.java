import java.io.*;
import java.util.*;

class Stock {
    String symbol;
    double price;

    Stock(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
    }
}

class User {
    String name;
    double balance;
    HashMap<String, Integer> portfolio = new HashMap<>();

    User(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    double getPortfolioValue(HashMap<String, Stock> market) {
        double value = balance;
        for (String s : portfolio.keySet()) {
            value += portfolio.get(s) * market.get(s).price;
        }
        return value;
    }
}

public class StockTradingPlatform {
    static Scanner sc = new Scanner(System.in);
    static HashMap<String, Stock> market = new HashMap<>();
    static ArrayList<User> users = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        // Initialize market
        market.put("AAPL", new Stock("AAPL", 150));
        market.put("TSLA", new Stock("TSLA", 200));
        market.put("GOOG", new Stock("GOOG", 120));

        users.add(new User("Alice", 1000));
        users.add(new User("Bob", 1500));

        while (true) {
            updateMarketPrices();
            System.out.println("\n--- Stock Trading Platform ---");
            System.out.println("1. View Market");
            System.out.println("2. Trade Stock");
            System.out.println("3. View Portfolio");
            System.out.println("4. Leaderboard");
            System.out.println("5. Save Portfolios");
            System.out.println("6. Exit");
            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> viewMarket();
                case 2 -> tradeStock();
                case 3 -> viewPortfolio();
                case 4 -> leaderboard();
                case 5 -> savePortfolios();
                case 6 -> System.exit(0);
            }
        }
    }

    static void updateMarketPrices() {
        Random rand = new Random();
        for (Stock s : market.values()) {
            double change = (rand.nextDouble() - 0.5) * 10;
            s.price = Math.max(1, s.price + change);
        }
    }

    static void viewMarket() {
        System.out.println("Current Market:");
        for (Stock s : market.values()) {
            System.out.println(s.symbol + " : $" + String.format("%.2f", s.price));
        }
    }

    static void tradeStock() {
        System.out.println("Select User (0 = Alice, 1 = Bob): ");
        int uid = sc.nextInt();
        User u = users.get(uid);

        System.out.println("Enter Stock Symbol: ");
        String sym = sc.next();
        Stock s = market.get(sym);

        System.out.println("1. Buy | 2. Sell");
        int choice = sc.nextInt();
        System.out.println("Enter Quantity: ");
        int qty = sc.nextInt();

        if (choice == 1) {
            double cost = qty * s.price;
            if (u.balance >= cost) {
                u.balance -= cost;
                u.portfolio.put(sym, u.portfolio.getOrDefault(sym, 0) + qty);
                System.out.println("Bought " + qty + " " + sym);
            } else System.out.println("Insufficient balance.");
        } else {
            int owned = u.portfolio.getOrDefault(sym, 0);
            if (owned >= qty) {
                u.portfolio.put(sym, owned - qty);
                u.balance += qty * s.price;
                System.out.println("Sold " + qty + " " + sym);
            } else System.out.println("Not enough shares.");
        }
    }

    static void viewPortfolio() {
        System.out.println("Select User (0 = Alice, 1 = Bob): ");
        int uid = sc.nextInt();
        User u = users.get(uid);

        System.out.println("Portfolio of " + u.name);
        for (String sym : u.portfolio.keySet()) {
            System.out.println(sym + " : " + u.portfolio.get(sym));
        }
        System.out.println("Balance: $" + String.format("%.2f", u.balance));
        System.out.println("Total Value: $" + String.format("%.2f", u.getPortfolioValue(market)));
    }

    static void leaderboard() {
        users.sort((a, b) -> Double.compare(b.getPortfolioValue(market), a.getPortfolioValue(market)));
        System.out.println("--- Leaderboard ---");
        for (User u : users) {
            System.out.println(u.name + " : $" + String.format("%.2f", u.getPortfolioValue(market)));
        }
    }

    static void savePortfolios() throws IOException {
        FileWriter fw = new FileWriter("Portfolios.txt");
        for (User u : users) {
            fw.write(u.name + " | Balance: " + u.balance + " | Portfolio: " + u.portfolio + "\n");
        }
        fw.close();
        System.out.println("Portfolios saved to file.");
    }
}
