package main.kiwitor.nomad.process;

public class CityProcess implements Runnable {
    private int index;

    public CityProcess(int index) {
        this.index = index;
    }

    @Override
    public void run() {
        System.out.println(index);
    }
}
