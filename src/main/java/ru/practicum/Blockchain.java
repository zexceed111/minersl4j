package ru.practicum;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Blockchain {



    public static final int HEADER_LENGTH = 6;

    private int nextShift = HEADER_LENGTH;

    private final int[] nextHeader = new int[HEADER_LENGTH];

    private LinkedList<Block> blocks = new LinkedList<>();

    public Blockchain(){
        System.out.println("Created a blockchain structure.");
        Block initialBlock = new Block();
        initialBlock.header = new int[HEADER_LENGTH];
        initialBlock.previousHashCode = 0;
        initialBlock.reward = 0;
        initialBlock.winner = "practicum";
        System.out.println("Created initial block "+initialBlock.hashCode());
        blocks.add(initialBlock);
        System.out.println("Added initial block");
        prepareNextHeader();
    }

    public void prepareNextHeader(){
        nextShift += (int) Math.round(Math.random() * 10) + 1;
        System.out.println("Next shift is "+nextShift);
        for(int n = nextShift; n < nextShift + HEADER_LENGTH; n++){
            nextHeader[n - nextShift] = calculatePhiNthDigit(n);
        }
        System.out.println("next header is " + Arrays.toString(nextHeader));
    }

    public Optional<Block> checkHeader(String nameCandidate, int[] headerCandidate){
        System.out.println("Candidate "+ nameCandidate + " wants to check block header " + Arrays.toString(headerCandidate));
        if(Objects.deepEquals(headerCandidate, nextHeader)) {
            System.out.println("Candidate " + nameCandidate + " is a winner");
            Block nextBlock = new Block();
            nextBlock.reward = 1;
            nextBlock.winner = nameCandidate;
            nextBlock.previousHashCode = blocks.getLast().hashCode();
            nextBlock.header = nextHeader;
            blocks.add(nextBlock);
            System.out.println("Created next block "+nextBlock.hashCode());
            prepareNextHeader();
            return Optional.of(nextBlock);
        } else {
            System.out.println("Candidate " + nameCandidate + " was wrong: "+Arrays.toString(headerCandidate) + " != "+Arrays.toString(nextHeader));
            return Optional.empty();
        }
    }

    public static class Block {
        private int[] header;

        private int previousHashCode;

        private int reward;

        private String winner;

        public int getReward() {
            return reward;
        }

        public String getWinner() {
            return winner;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Block block = (Block) o;
            return previousHashCode == block.previousHashCode && reward == block.reward && Arrays.equals(header, block.header) && Objects.equals(winner, block.winner);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(previousHashCode, reward, winner);
            result = 31 * result + Arrays.hashCode(header);
            return result;
        }
    }

    //это алгоритм, вычисляющий n-ый знак одной знаменитой
    // математической константы, которая известна как "золотое сечение"
    private static int calculatePhiNthDigit(int n) {
        MathContext mc = new MathContext(n + 2);
        BigDecimal a = BigDecimal.ZERO;
        BigDecimal b = BigDecimal.ONE;
        BigDecimal phiApprox;

        for (int i = 0; i < n * n; i++) {
            BigDecimal nextFib = a.add(b);
            a = b;
            b = nextFib;
        }
        phiApprox = b.divide(a, mc);

        BigDecimal tenPowerN = BigDecimal.TEN.pow(n);
        phiApprox = phiApprox.multiply(tenPowerN);

        String phiString = phiApprox.toPlainString();
        int decimalIndex = phiString.indexOf('.');
        int nthIndex = decimalIndex - 1;
        char nthDigitChar = phiString.charAt(nthIndex);
        return Character.getNumericValue(nthDigitChar);
    }

}
