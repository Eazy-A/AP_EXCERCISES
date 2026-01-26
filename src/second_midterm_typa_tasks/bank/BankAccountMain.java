package second_midterm_typa_tasks.bank;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class BankAccount{
    private final int id;
    private double amount;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public BankAccount(int id, double amount) {
        this.id = id;
        this.amount = amount;
    }

    public boolean deposit(double amount){
        lock.writeLock().lock();
        try{
        this.amount += amount;
        }finally {
            lock.writeLock().unlock();
        }
        return true;
    }
    public boolean withdraw(double amount){
        lock.writeLock().lock();
        try{
            if (this.amount < amount) return false;
            this.amount -= amount;
        }finally {
            lock.writeLock().unlock();
        }
        return true;
    }
    public double getBalance(){
        lock.readLock().lock();
        try{
            return this.amount;
        }finally {
            lock.readLock().unlock();
        }
    }
    public boolean transfer(BankAccount target, double amount){
        if (lock.writeLock().tryLock()){
            try{
                if(target.lock.writeLock().tryLock()){
                    try{
                        if(this.amount >= amount){
                            this.amount -= amount;
                            target.amount += amount;
                            return true;
                        }
                        return false;
                    }finally {
                        target.lock.writeLock().unlock();
                    }
                }
                return false;
            }finally {
                lock.writeLock().unlock();
            }
        }
        return false;
    }

    public int getId() {
        return id;
    }
}
class TransferTask implements Callable<String>{
    private final BankAccount fromAccount;
    private final BankAccount toAccount;
    private final double amount;

    public TransferTask(BankAccount fromAccount, BankAccount toAccount, double amount) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
    }
    public String success(){
        if (fromAccount.transfer(toAccount,amount)) return "Success";
        return "Failed";
    }
    @Override
    public String call() throws Exception {
        return String.format("TRANSFER %.2f from Account-%d to Account-%d. Result: %s"
                ,amount, fromAccount.getId(), toAccount.getId(), success());
    }
}
class BalanceCheckTask implements Callable<String>{
    private final BankAccount account;

    public BalanceCheckTask(BankAccount account) {
        this.account = account;
    }

    @Override
    public String call() throws Exception {
        return String.format("Account-%d balance: %.2f",account.getId(), account.getBalance());
    }
}
class BankAccountMain {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        int numAccounts = sc.nextInt();
        int numTransfers = sc.nextInt();
        int numThreads = sc.nextInt();

        // Create accounts with initial balance
        List<BankAccount> accounts = new ArrayList<>();
        for (int i = 0; i < numAccounts; i++) {
            accounts.add(new BankAccount(i, 1000.0));
        }

        // Calculate initial total
        double initialTotal = accounts.stream()
                .mapToDouble(BankAccount::getBalance)
                .sum();

        List<Callable<String>> tasks = new ArrayList<>();
        Random random = new Random();

        // Create transfer tasks
        for (int i = 0; i < numTransfers; i++) {
            int from = random.nextInt(numAccounts);
            int to = random.nextInt(numAccounts);
            while (to == from) {
                to = random.nextInt(numAccounts);
            }
            double amount = random.nextDouble() * 100;
            tasks.add(new TransferTask(accounts.get(from), accounts.get(to), amount));
        }

        // Add balance check tasks
        for (int i = 0; i < numAccounts; i++) {
            tasks.add(new BalanceCheckTask(accounts.get(i)));
        }

        // Execute all tasks
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<String>> results = executor.invokeAll(tasks);
        executor.shutdown();

        // Print results
        for (Future<String> result : results) {
            System.out.println(result.get());
        }

        // Verify total balance unchanged
        double finalTotal = accounts.stream()
                .mapToDouble(BankAccount::getBalance)
                .sum();

        if (Math.abs(initialTotal - finalTotal) < 0.01) {
            System.out.println("✔ TOTAL BALANCE PRESERVED");
        } else {
            System.out.println("✗ ERROR: Money lost or created!");
        }
    }
}
