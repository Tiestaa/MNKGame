/*
 * created by Francesco Testa, Pietro Sami
 */
package mnkgame;

public class DataHash {
    private final Flag flag;
    private final int depth;
    private final int valuation;

    public DataHash(int depth, int valuation, Flag flag) {
        this.flag=flag;
        this.depth = depth;
        this.valuation = valuation;
    }
    public Flag getFlag() {
        return flag;
    }
    public int getDepth() {
        return depth;
    }
    public int getValuation() {
        return valuation;
    }


}
