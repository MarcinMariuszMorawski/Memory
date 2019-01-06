package pl.lodz.uni.math.morawski.marcin.memory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;


public final class MemoryButton extends android.support.v7.widget.AppCompatButton {

    private BitmapDrawable front;
    private BitmapDrawable back;

    private boolean isReverted = false;
    private boolean isMatched = false;

    MemoryButton(Context context) {
        super(context);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.questionmark);
        back = new BitmapDrawable(getResources(), bitmap);
        setBackground(back);
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setFront(BitmapDrawable front) {
        this.front = front;
    }

    public void match(){
        isMatched = true;
    }

    public void revertToBack(){
        isReverted=false;
        setBackground(back);
    }

    public boolean revertToFront(){

        if(isMatched) {
            return false;
        }

        if(isReverted){
            return false;
        }

        isReverted=true;
        setBackground(front);
        return true;

    }

    public BitmapDrawable getFront() {
        return front;
    }
}
