package hapkiduki.net.empresis.clases;

import android.view.View;

/**
 * Created by Programa-PC on 22/03/2017.
 */

public interface RecyclerClick {

    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
