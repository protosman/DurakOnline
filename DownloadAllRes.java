package ru.tmkstd.cardgamedurakonline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DownloadAllRes {
    ButtonMy bitoBtn, beryBtn, passBtn,resetBtn, returnCardBtn, addFriendBtn, btnExit, btnFlag;
    Card[] imageCard;
    Bitmap grandPanelGameField;
    Bitmap yourTurn, wait_oppon_pass,passChat,bitoChat, beryChat, findGame
                ;
    Bitmap mastBubi, mastChervi, mastTrefy, mastVini, avatar,avatarOpponMenu,
            avatarTimer,
            coinAdd;
    Bitmap grandFlame,
            flag;
    Bitmap whiteRamka,whiteRamkaPerevod;
    Bitmap connection, icon_exit, ok;
    Card[][]  emojis;
    Card[] emojisMark;
    Card[] rubashka;
    String kolodaSorting;

    Context context;
    final int maxX,maxY,sizeCard,sizeCardHeight;
    final int kolodaRandomPosX, kolodaRamdomPosY,kolodaRandomScale;

    DownloadAllRes(Context c, int sizeDispX, int sizeDispY) {
        Log.d("LLL1","DownloadAllRes");
        context = c;
        maxX = sizeDispX;
        maxY = sizeDispY;
        sizeCard = (int)(Math.sqrt(maxX*maxX+maxY*maxY)/6);
        sizeCardHeight = sizeCard*14/10;
        kolodaSorting = "MIEAwsokgc840NJFBxtplhd951OKGCyuqmiea62PLHDzvrnjfb73";
        imageCard = new Card[52];
        rubashka = new Card[53];
        connection = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.icon_connection),
                maxY/9,maxY/18 ,false);
        icon_exit = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.icon_exit),
            maxY/18,maxY/18,false);
        ok = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.icon_ok),
                maxY/9,maxY/9 ,false);
        btnExit = new ButtonMy(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.btn_exit_field),
                        maxY/15,maxY/15 ,false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.btn_exit_field),
                        maxY/15,maxY/15 ,false)
        );
        btnFlag = new ButtonMy(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.btn_flag_field),
                        maxY/15,maxY/15 ,false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.btn_flag_field),
                        maxY/15,maxY/15 ,false)
        );
        avatarTimer=  Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.num_fon),
                maxY/9,maxY/9 ,false);
        whiteRamka = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.whiteramka),
                sizeCard*6/10, sizeCardHeight*6/10,false);
        whiteRamkaPerevod = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.whiteramkaperevod),
                sizeCard*6/10, sizeCardHeight*6/10,false);
        flag = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.flag),
                maxX*15/50, maxX*15/50,false);
        coinAdd = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.coin),
                maxX/15, maxX/15,false);
        avatar = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.avatar_two),
                maxY/9, maxY/9,false);
        avatarOpponMenu = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.avatar_two),
                maxY/8, maxY/8,false);
        mastBubi = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.bubi),
                sizeCardHeight*6/20, sizeCardHeight*6/20,false);
        mastChervi = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.chervi),
                sizeCardHeight*6/20, sizeCardHeight*6/20,false);
        mastTrefy = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.trefy),
                sizeCardHeight*6/20, sizeCardHeight*6/20,false);
        mastVini = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.vini),
                sizeCardHeight*6/20, sizeCardHeight*6/20,false);
        findGame = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.find_game),
                maxX, maxX*10/25,false);
        yourTurn = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.your_turn),
                maxY*2/9, maxY/18,false);
        wait_oppon_pass = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.waiting_oppon_pass),
                maxY*5/18, maxY/18,false);
        beryChat= Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.bery_chat),
                maxY/9, maxY/9,false);
        bitoChat= Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.bito_chat),
                maxY/9, maxY/9,false);
        passChat= Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.pass_chat),
                maxY/9, maxY/9,false);

        downloadImageCard();
        downloadBtn();
        downloadEmoji();

        for (int i =0; i < kolodaSorting.length(); i++){
            imageCard[i].setNickName(kolodaSorting.charAt(i));
        }

        kolodaRandomPosX=maxX-rubashka[52].getWidth()/2;
        kolodaRamdomPosY=(maxY-maxY/9)/2-rubashka[52].getHeight()+rubashka[52].getWidth()/2;
        kolodaRandomScale=(maxY-maxY/9)/2;
    }
    private void downloadBtn() {
        addFriendBtn = new ButtonMy(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.btn_gamefield_add_friends),
                        maxY/9*12/12, maxY/9*12/24,false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.btn_gamefield_add_friends_press),
                        maxY/9*12/12, maxY/9*12/24,false));
        resetBtn = new ButtonMy(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.btn_gamefield_reset),
                        maxY/9*12/8, maxY/9*12/16,false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.btn_gamefield_reset_press),
                        maxY/9*12/8, maxY/9*12/16,false));
        grandPanelGameField =
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.gamefield_grand_panel),
                        maxY/3, maxY*2/9,true);
        grandFlame = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.gamefield_grand_flame),
                maxY/2, maxY/7,false);
        returnCardBtn = new ButtonMy(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.btn_gamefield_returncard),
                        maxY/9*10/13, maxY/9*10/13,false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.btn_gamefield_returncard_press),
                        maxY/9*10/13, maxY/9*10/13,false));
        beryBtn = new ButtonMy(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.btn_gamefield_bery),
                        maxY/9, maxY/9*12/16,false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.btn_gamefield_bery_press),
                        maxY/9, maxY/9*12/16,false));
        passBtn = new ButtonMy(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.btn_gamefield_pass),
                        maxY/9, maxY/9*12/16,false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.btn_gamefield_pass_press),
                        maxY/9, maxY/9*12/16,false));
        bitoBtn = new ButtonMy(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.btn_gamefield_bito),
                        maxY/9, maxY/9*10/13,false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.btn_gamefield_bito_press),
                        maxY/9, maxY/9*12/16,false));
        returnCardBtn.setPosBtnY(maxY-sizeCardHeight - returnCardBtn.getHeight());
        resetBtn.setPosBtnY(maxY/2);
        addFriendBtn.setPosBtnY( (maxY-grandPanelGameField.getHeight())/2 + grandPanelGameField.getHeight()/10+maxY/8-addFriendBtn.getHeight());

        bitoBtn.setPosBtnY(maxY-sizeCardHeight - bitoBtn.getHeight());
        passBtn.setPosBtnY(maxY-sizeCardHeight - passBtn.getHeight());
        beryBtn.setPosBtnY(maxY-sizeCardHeight - beryBtn.getHeight());
        returnCardBtn.setPosBtnX(maxX);
        resetBtn.setPosBtnX(maxX);
        bitoBtn.setPosBtnX(maxX);
        beryBtn.setPosBtnX(maxX);
        passBtn.setPosBtnX(maxX);
    }
    private void downloadImageCard() {
        rubashka[0] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.rubashka),
                        sizeCard/4, sizeCardHeight/4, true), maxX, maxY);
        for (int i =1; i< 52;i++) {

            rubashka[i] = new Card(sizeCard/4,sizeCardHeight/4,maxX, maxY);
        }
        rubashka[52] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.rubashka),
                        (int)(sizeCard*0.6), (int)(sizeCardHeight * 0.6), true), maxX, maxY);
        rubashka[52].setPosBtnX(maxX - rubashka[52].getWidth() / 2);
//        rubashka[52].setPosBtnY((maxY - rubashka[52].getHeight() - bottonBar.getHeight()) / 2);
        rubashka[52].setPosBtnY(0);
        imageCard[0] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.twob),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[1] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.threeb),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[2] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.fourb),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[3] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.fiveb),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[4] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.sixb),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[5] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.sevenb),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[6] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.eightb),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[7] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.nineb),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[8] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.tenb),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[9] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.jb),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[10] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.qb),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[11] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.kb),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[12] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.tb),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[13] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.twot),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[14] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.threet),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[15] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.fourt),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[16] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.fivet),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[17] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.sixt),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[18] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.sevent),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[19] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.eightt),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[20] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.ninet),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[21] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.tent),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[22] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.jt),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[23] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.qt),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[24] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.kt),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[25] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.tt),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[26] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.twoj),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[27] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.threej),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[28] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.fourj),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[29] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.fivej),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[30] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.sixj),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[31] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.sevenj),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[32] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.eightj),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[33] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.ninej),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[34] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.tenj),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[35] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.jj),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[36] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.qj),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[37] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.kj),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[38] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.tj),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[39] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.twov),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[40] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.threev),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[41] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.fourv),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[42] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.fivev),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[43] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.sixv),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[44] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.sevenv),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[45] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.eightv),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[46] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.ninev),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[47] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.tenv),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[48] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.jv),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[49] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.qv),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[50] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.kv),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
        imageCard[51] = new Card(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.tv),
                        sizeCard, sizeCardHeight,true),maxX,maxY);
    }
    private void downloadEmoji() {
        emojis = new Card[4][6];
        emojis[0][0] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.a),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[0][1] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.b),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[0][2] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.c),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[0][3] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.d),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[0][4] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.e),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[0][5] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.f),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[0][0].emojiName = "a";
        emojis[0][1].emojiName = "b";
        emojis[0][2].emojiName = "c";
        emojis[0][3].emojiName = "d";
        emojis[0][4].emojiName = "e";
        emojis[0][5].emojiName = "f";

        emojis[1][0] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.aa),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[1][1] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ab),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[1][2] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ac),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[1][3] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ad),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[1][4] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ae),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[1][5] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.af),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[1][0].emojiName = "aa";
        emojis[1][1].emojiName = "ab";
        emojis[1][2].emojiName = "ac";
        emojis[1][3].emojiName = "ad";
        emojis[1][4].emojiName = "ae";
        emojis[1][5].emojiName = "af";


        emojis[2][0] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ba),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[2][1] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.bb),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[2][2] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.bc),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[2][3] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.bd),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[2][4] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.be),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[2][5] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.bf),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[2][0].emojiName = "ba";
        emojis[2][1].emojiName = "bb";
        emojis[2][2].emojiName = "bc";
        emojis[2][3].emojiName = "bd";
        emojis[2][4].emojiName = "be";
        emojis[2][5].emojiName = "bf";


        emojis[3][0] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ca),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[3][1] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.cb),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[3][2] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.cc),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[3][3] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.cd),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[3][4] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ce),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[3][5] = new Card(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.cf),
                maxY/9,maxY/9 ,true),maxX,maxY);
        emojis[3][0].emojiName = "ca";
        emojis[3][1].emojiName = "cb";
        emojis[3][2].emojiName = "cc";
        emojis[3][3].emojiName = "cd";
        emojis[3][4].emojiName = "ce";
        emojis[3][5].emojiName = "cf";
//
//        emojis[0][0].setPosBtnX(maxX - maxY/9);
//        emojis[0][1].setPosBtnX(maxX - maxY*2/9);
//        emojis[0][2].setPosBtnX(maxX - maxY*3/9);
//        emojis[0][3].setPosBtnX(maxX - maxY/9);
//        emojis[0][4].setPosBtnX(maxX - maxY*2/9);
//        emojis[0][5].setPosBtnX(maxX - maxY*3/9);
//        emojis[0][0].setPosBtnY(maxY-maxY*2/9);
//        emojis[0][1].setPosBtnY(maxY-maxY*2/9);
//        emojis[0][2].setPosBtnY(maxY-maxY*2/9);
//        emojis[0][3].setPosBtnY(maxY-maxY/9);
//        emojis[0][4].setPosBtnY(maxY-maxY/9);
//        emojis[0][5].setPosBtnY(maxY-maxY/9);
        emojis[0][0].setPosBtnX(maxX/2-maxY/18 - maxY/9);
        emojis[0][1].setPosBtnX(maxX/2-maxY/18);
        emojis[0][2].setPosBtnX(maxX/2-maxY/18 + maxY/9);
        emojis[0][3].setPosBtnX(maxX/2-maxY/18 - maxY/9);
        emojis[0][4].setPosBtnX(maxX/2-maxY/18);
        emojis[0][5].setPosBtnX(maxX/2-maxY/18 + maxY/9);
        emojis[0][0].setPosBtnY(maxY/2-maxY/9);
        emojis[0][1].setPosBtnY(maxY/2-maxY/9);
        emojis[0][2].setPosBtnY(maxY/2-maxY/9);
        emojis[0][3].setPosBtnY(maxY/2);
        emojis[0][4].setPosBtnY(maxY/2);
        emojis[0][5].setPosBtnY(maxY/2);
        for(int i = 1; i < emojis.length;i++) {
            for(int j = 0;j < emojis[i].length;j++) {
                emojis[i][j].setPosBtnX(emojis[0][j].getPosX());
                emojis[i][j].setPosBtnY(emojis[0][j].getPosY());
            }
        }

        emojisMark = new Card[emojis.length];
        for(int i = 0; i < emojisMark.length; i++) {
            emojisMark[i] = new Card(Bitmap.createScaledBitmap(emojis[i][0].normal,maxY/18,maxY/18,true),maxX,maxY);
            emojisMark[i].setPosBtnY(maxY/2+grandPanelGameField.getHeight()/2);
            emojisMark[i].setPosBtnX(maxX/2-(maxY/36+maxY/18)*emojisMark.length/2 + (maxY/36+maxY/18)*i + maxY/72);
        }
    }
    void resetPosition(){
        for(int i =0; i< imageCard.length;i++) {
            imageCard[i].setPosBtnX(maxX-imageCard[i].getHeight()/2);
            imageCard[i].setPosBtnY(sizeCard/2);
            rubashka[i].setPosBtnX(maxX);
            rubashka[i].setPosBtnY(sizeCard/2);
        }

        resetBtn.setPosBtnX(maxX);
        bitoBtn.setPosBtnX(maxX);
        beryBtn.setPosBtnX(maxX);
        passBtn.setPosBtnX(maxX);

    }
}
