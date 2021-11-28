public class Main {
    public static void main(String[] args) {
        MyFeal4Output myFeal4Output = new MyFeal4Output();
        myFeal4Output.readKnownTextPairs();

        System.out.println("Start Linear Analysis of Feal 4");

//k1 = 4096
        for (byte a0 = -128; a0 < 127; a0++) {
            for (byte a1 = -128; a1 < 127; a1++) {
                int counter_1 = 0, counter_2 = 0;

                for (int innerLoop = 0; innerLoop < myFeal4Output.getNUMBER_OF_PAIRS(); innerLoop++) {
                    int a = myFeal4Output.pairSplitA0A1(innerLoop, a0, a1);
                    if (a == 0) {
                        counter_1++;
                    } else if (a == 1) {
                        counter_2++;
                    }
                }
                if (counter_1 == myFeal4Output.getNUMBER_OF_PAIRS() || counter_2 == myFeal4Output.getNUMBER_OF_PAIRS()) {
                    myFeal4Output.printBytes(a0, a1);
                }
            }

        }

        System.out.println("Finish Linear Analysis of Feal 4");
    }
}
