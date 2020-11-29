package gaoxuanli.dss.sales.util;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

public class MultiplyLinearRegression {

    DenseMatrix64F X, Y, B;

    public MultiplyLinearRegression(double[][] x_value, double[] y_value) {
        int row = x_value.length;
        X = new DenseMatrix64F(row, x_value[0].length + 1);
        Y = new DenseMatrix64F(row, 1);
        B = new DenseMatrix64F(x_value[0].length + 1, 1);

        // set data
        for (int i = 0; i < row; i++) {
            Y.set(i, 0, y_value[i]);
            X.set(i, 0, 1.0);
            for (int j = 1; j < X.numCols; j++) {
                X.set(i, j, x_value[i][j - 1]);
            }
        }
        System.out.println("X: " + X);
        System.out.println("Y: " + Y);
    }

    public double[] doCalculate() {
        DenseMatrix64F X_trans = new DenseMatrix64F(X.numCols, X.numRows);
        CommonOps.transpose(X, X_trans);
        System.out.println("X_trans: " + X_trans);

        DenseMatrix64F X_trans_mult_X = new DenseMatrix64F(X.numCols, X.numCols);
        CommonOps.mult(X_trans, X, X_trans_mult_X);
        System.out.println("X_trans * X: " + X_trans_mult_X);
        CommonOps.invert(X_trans_mult_X); // (XT * X)^-1
        System.out.println("inv(X_trans * X): " + X_trans_mult_X);

        DenseMatrix64F X_trans_mult_Y = new DenseMatrix64F(X.numCols, Y.numCols);
        CommonOps.mult(X_trans, Y, X_trans_mult_Y);
        System.out.println("X_trans * Y: " + X_trans_mult_Y);

        CommonOps.mult(X_trans_mult_X, X_trans_mult_Y, B);
        System.out.println("B: " + B);
        return B.getData();
    }

}
