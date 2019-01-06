package pl.lodz.uni.math.morawski.marcin.memory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;


public final class MemoryButton extends android.support.v7.widget.AppCompatButton {

    private BitmapDrawable photo;
    private BitmapDrawable questionmark;

    private boolean isReverted = false;
    private boolean isMatched = false;

    MemoryButton(Context context) {
        super(context);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.questionmark);
        questionmark = new BitmapDrawable(getResources(), bitmap);
        setBackground(questionmark);
    }

    public boolean tryRevertToFront(){

        if(isMatched) {
            return false;
        }

        if(isReverted){
            return false;
        }

        isReverted=true;
        setBackground(photo);
        return true;
    }

    public void revertToBack(){
        isReverted=false;
        setBackground(questionmark);
    }

    public BitmapDrawable getPhoto() {
        return photo;
    }

    public void setPhoto(BitmapDrawable photo) {
        this.photo = photo;
    }

    public void match(){
        isMatched = true;
    }
    public boolean isMatched() {
        return isMatched;
    }
}