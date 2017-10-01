package de.jbamberger.irremote.service.ir;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import de.jbamberger.irremote.R;

/**
 * Created: 04.01.2016
 *
 * @author Jannik
 * @version 04.01.2016
 */
public class IrTools {

    public static Intent generateIRSenderIntent(Context context, int code) {
        Intent intent = new Intent();
        intent.setAction(context.getString(R.string.intentfilter_send_code));
        intent.putExtra("" /*FIXME: use name..*/, code);
        return intent;
    }

    private int[] generateCode(int[] c) {

        /*//generate all codes:
        for (int[] i : codePatterns) {
            int[] code = generateCode(i);
            for (int time : code) {
                System.out.print(time + ", ");
            }
            System.out.print("\n");
        }*/


        ArrayList<Integer> code = new ArrayList<>();
        code.add(9000);
        code.add(4500);
        int ai[] = new int[4];
        ai[0] = c[0];
        ai[1] = c[1];
        ai[2] = c[2];
        ai[3] = ~c[2];
        for (int j1 : ai) {
            int l = 0;
            while (l < 8) {
                code.add(560);
                char c1;
                if ((128 >> l & j1) == 0) {
                    c1 = '\u0230';
                } else {
                    c1 = '\u069A';
                }
                code.add((int) c1);
                l++;
            }
        }

        code.add(560);
        int[] arr = new int[code.size()];
        for (int k = 0; k < code.size(); k++) {
            arr[k] = code.get(k);
        }

        return arr;
    }
}
