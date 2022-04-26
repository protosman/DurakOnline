package ru.tmkstd.cardgamedurakonline;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class RobotGameAI {
    boolean modGame, hodit;
    boolean iniciatorBito = false;
    boolean endGame = false;
    boolean iBery = false;
//    boolean waitBito = false; // оставил до лучших времён
//    int countWaitBito = 0;
    int koziri;
    int lenOpponent, lenHodit,lenOtbivaet;
    int[] robotParams;
    String myCards, hoditCards ="", otbivaetCards="", kolodaRandom;
    String myCardsBeforTurn;
    String kolodaSorting = "MIEAwsokgc840NJFBxtplhd951OKGCyuqmiea62PLHDzvrnjfb73";
    String koloda52 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOP";
    String tempProverkaCards, tempOtbivaetCards;
    public RobotGameAI(boolean mod,int[] params) {
        robotParams = params;
        modGame = mod;
    }
    void setMyCards(String cards){
        myCards = cards;
        myCardsBeforTurn = cards;
    }
    int setHoditCards(String cards) {
        if(cards == null) cards = "";
        if(cards.length() == 0) {
            hoditCards = cards;
            return 10;
        }
        if(cards.length()>1 && hodit && !cards.equals(hoditCards)) {
            int error = -1;
            for(int i = 1; i<cards.length() && i<hoditCards.length();i++){
                if(cards.charAt(i)!=hoditCards.charAt(i)){
                    error = i-1;
                    break;
                }
            }
            if(error!=-1) {
//                Проверка на банальный перевод карты
//                если данные не совпадают, а ошибки нет, значить данные прост добавили, значит перевели карту
                if (myCardsBeforTurn.indexOf(cards.charAt(error)) > -1) {
                    //значит я хожу и настаиваю на своём ходе
                    hodit = true;
                    return 0;
                } else {
                    //Значит я переводил и вынужден вернуть карты
                    for (int i = error + 1; i < hoditCards.length(); i++) {
                        myCards = myCards + hoditCards.charAt(i);
                    }
                    hodit = false;
                    sortingCard();
                }
            }
        }
        hoditCards = cards;
        //Проверка на перевод карты
        if(modGame && hoditCards.length()>1) {
            if(myCardsBeforTurn.indexOf(hoditCards.charAt(hoditCards.length()-1)) >-1){
                //значит это  я походил и мне не перевели
                hodit = true;
            }
            else {
                hodit = false;
                return 1;
            }
        }
        return 10;
    }
    void setOtbivaetCards(String cards) {
        if(cards == null) otbivaetCards = "";
        else otbivaetCards = cards;
    }
    void setKoziri(int koz) {
        koziri = koz;
    }
    void setHod(boolean hod) {
        hodit = hod;
//        countWaitBito = 0;
//        if(hod) waitBito = false;
//        else waitBito = true;
    }
    boolean setKolodaRandom(String koloda) {
        if(koloda == null) kolodaRandom = "";
        else kolodaRandom = koloda;
//        if(waitBito) {
//            countWaitBito++;
//            if(countWaitBito==2){
//                countWaitBito = 0;
//                waitBito = false;
//                rasdacha(kolodaRandom);
//                return true;//сделать раздачу карт
//            }
//        }
        return false;
    }
    void setLenOpponent(int len) {
        lenOpponent = len;
    }
    void setLenHodit(int len){
        lenHodit = len;
    }
    void setLenOtbivaet(int len) {
        lenOtbivaet = len;
    }
    int bito(){
        hoditCards = "";
        otbivaetCards = "";
        if (endGame) return 10;
        if(kolodaRandom.length() == 0 && myCards.length() == 0 && lenOtbivaet == 0){
            return 98;
        }
        if(!hodit) {
//            if(myCards.length() == 0 && kolodaRandom.length() +lenOpponent <=6){
//                return 99;
////                может здесь этого не делать т.к. в робот отбивает можно сделать эту проверку
//            }
            hodit = true;
//            if(waitBito) {
//                countWaitBito++;
//                if(countWaitBito==2){
//                    countWaitBito = 0;
//                    waitBito = false;
                    rasdacha(kolodaRandom);
//                }
//            }
            return 0;//надо сбросить все параметры в Bito
        }
//        waitBito = true;
//        countWaitBito = -1;//т.к. тот кто отбивал сейчас забьёт колоду и я получу сразу +1 . хотя блин я ведь ничего не получу если колода = 0 она не измениться
        iniciatorBito = false;
        hodit = false;
        return 1;
    }
    int bery() {

        if (endGame) return 10;
        if(hodit){
            if(myCards.length() == 0 && kolodaRandom.length() == 0) {
                return 99;
            }
            if(proverkaPodkod()) {
                for(char i: hoditCards.toCharArray()){
                    myCards = myCards.replaceAll(String.valueOf(i),"");
                }
                for(char i: otbivaetCards.toCharArray()){
                    myCards = myCards.replaceAll(String.valueOf(i),"");
                }
//                hoditCards = "";
//                otbivaetCards = "";
                rasdacha(kolodaRandom);
                return 1;
            }
            for(char i: hoditCards.toCharArray()){
                myCards = myCards.replaceAll(String.valueOf(i),"");
            }
            for(char i: otbivaetCards.toCharArray()){
                myCards = myCards.replaceAll(String.valueOf(i),"");
            }
//                hoditCards = "";
//                otbivaetCards = "";
            rasdacha(kolodaRandom);
            return  0;// так как есть серьёзные проблемы с порядком есле человек не получит данные с ходитКарт сразу получит Пасс то игра улетит нах
            // поэто надо повременить с этим и сделать двух контактную проверку на получения данных. также как с сетКолодРандом
//            if(proverkaPodkod()) return 1;//int подкид
//            return 0;//int назначить пасс
        }
        else {
            if(kolodaRandom.length() == 0 && lenHodit == 0){
                return 98;
            }
        }
//            waitBito = true;
//            countWaitBito = 0;
            //Значит я беру
            //Жду когда нажмут ПАСС
//Надо сделать return и int чтобы дать знать Field что делать
        return 10;
    }
    int pass() {
        iBery = false;
//        try {
//            Thread.sleep(1000); //Приостанавливает поток на 1 секунду
//        } catch (Exception e) {
//
//        }
        if (endGame) return 10;
        if(!hodit) {
            //Я взял
            myCards=myCards + hoditCards + otbivaetCards;
            myCardsBeforTurn = myCards;
            sortingCard();
            hoditCards = "";
            otbivaetCards = "";
            return 0;
        }

//        for(char i: hoditCards.toCharArray()){
//            myCards = myCards.replaceAll(String.valueOf(i),"");
//        }
//        for(char i: otbivaetCards.toCharArray()){
//            myCards = myCards.replaceAll(String.valueOf(i),"");
//        }
//        hoditCards = "";
//        otbivaetCards = "";
        return 1;

    }
    boolean rasdacha(String koloda) {
        if(koloda!=null) {
            if (myCards.length()<6) {
                while (myCards.length() < 6 && kolodaRandom.length() > 0) {
                    char i = kolodaRandom.charAt(kolodaRandom.length() - 1);
                    myCards = myCards + i;
                    kolodaRandom = kolodaRandom.replaceAll(String.valueOf(i), "");
                }
            }
        }
        myCardsBeforTurn = myCards;
        sortingCard();
        return true;
    }
    void endGame() {
        endGame = true;
    }
//    boolean sdelatHod() {
//        if(hodit) {
//            int minCard = -1;
//            for (int i = 0; i < myCards.length(); i++) {
//                if ((int) (kolodaSorting.indexOf(myCards.charAt(i)) / 13) != koziri && koloda52.indexOf(myCards.charAt(i)) > minCard) {
//                    minCard = i;
//                }
//            }
//            if (minCard != -1) {
//                hoditCards = hoditCards + myCards.indexOf(minCard);
//                myCards = myCards.substring(0,minCard) + myCards.substring(minCard+1);
//            }
//            else {
//                for (int i = 0; i < myCards.length(); i++) {
//                    if (koloda52.indexOf(myCards.charAt(i)) > minCard) {
//                        minCard = i;
//                    }
//                }
//                hoditCards = hoditCards + myCards.indexOf(minCard);
//                myCards = myCards.substring(0,minCard) + myCards.substring(minCard+1);
//            }
//            return true;
//        }
//        gotovOtbivat();
//
//
//        return false;
//    }
//    boolean gotovOtbivat() {
//
//        return false;
//    }
    boolean allBito() {
        if(hoditCards.length() == otbivaetCards.length()) {
            if(otbivaetCards.indexOf(' ')!=-1)
                return false;
//            for (int i  = 0; i < otbivaetCards.length(); i++) {
//                if(otbivaetCards.charAt(i)== ' ') {
//                    return false;
//                }
//            }
            return true;
        }
        return false;
    }
    int robotOtbivaet() {
        Log.d("Gluuuk","O " + otbivaetCards);
        if(hoditCards.length() == 0) return 10;
        if (iBery) return 10;

//        if(otbivaetCards != null && otbivaetCards.length()>0){
//            try {
//                Thread.sleep(1000); //Приостанавливает поток на 1 секунду
//            } catch (Exception e) {
//
//            }
//        }
        if (endGame) return 10;

//        timer = new Timer();
//        timerTask = new TimerTask() {
//            @Override
//            public void run() {
//            }
//        };
//        timer.schedule(timerTask,1000);
        if(!hodit){
            if(myCards.length() == 0 && kolodaRandom.length() !=0 && kolodaRandom.length() + lenOpponent <=6) {
                return 99;
            }
            if(allBito()) {
                return 10;
            }
            if(otbivaetCards.length() == 0)
                if(Math.random()*100<=robotParams[2]){
                    iBery = true;
                    return 2;//беру
                }
            if(perevod()) {

                return 1;
            }
            if(proverkaOtbiv()){
                //Отбить и удалить из mycards
                return 0;
            }
            iBery = true;
            return 2;//беру
        }
        return 10;
    }
    boolean proverkaOtbiv() {
        //Сделать Вероятность и в зависимости от типа робота тоже)))
        if(proverkaSmogyLiOtbitVse()) {
            for (int i = 0; i < hoditCards.length(); i++) {
                if (otbivaetCards.length() < i + 1) {
                    for (int j = 0; j < myCards.length(); j++) {
                        if (funOtbivProverka(kolodaSorting.indexOf(hoditCards.charAt(i)), kolodaSorting.indexOf(myCards.charAt(j)))) {
                            otbivaetCards = otbivaetCards + myCards.charAt(j);
                            myCards = myCards.replaceAll(String.valueOf(myCards.charAt(j)), "");
                            return true;
                        }
                    }
                    return false;
                } else {
                    if (otbivaetCards.charAt(i) == ' ') {
                        for (int j = 0; j < myCards.length(); j++) {
                            if (funOtbivProverka(kolodaSorting.indexOf(hoditCards.charAt(i)), kolodaSorting.indexOf(myCards.charAt(j)))) {
                                otbivaetCards = otbivaetCards.substring(0, i) + myCards.charAt(j) + otbivaetCards.substring(i + 1);
                                myCards = myCards.replaceAll(String.valueOf(myCards.charAt(j)), "");
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    boolean proverkaSmogyLiOtbitVse(){
        //Временно для робота котмэн чтобы не палиться что я делаю его умнее
        if(Math.random()*100>robotParams[4])return true;
        tempProverkaCards = myCards;
        tempOtbivaetCards = "";
        for(int i = 0; i<hoditCards.length();i++){
            if(tempOtbivaetCards.length()<i+1) {
                for(int j = 0; j < tempProverkaCards.length();j++){
//                    Log.d("ProverVse",funOtbivProverka(kolodaSorting.indexOf(hoditCards.charAt(i)),kolodaSorting.indexOf(tempProverkaCards.charAt(j)))+"");
//                    Log.d("ProverVse",kolodaSorting.indexOf(hoditCards.charAt(i)) + " " +kolodaSorting.indexOf(tempProverkaCards.charAt(j)));

                    if(funOtbivProverka(kolodaSorting.indexOf(hoditCards.charAt(i)),kolodaSorting.indexOf(tempProverkaCards.charAt(j)))){
                        tempOtbivaetCards = tempOtbivaetCards + tempProverkaCards.charAt(j);
                        tempProverkaCards = tempProverkaCards.replaceAll(String.valueOf((tempProverkaCards.charAt(j))),"");
                        break;
                    }
//                    else {
//                        return false;
//                    }
                }
            }
            else {
                if(tempOtbivaetCards.charAt(i)==' '){
                    for(int j = 0;j<tempProverkaCards.length();j++){
                        if(funOtbivProverka(kolodaSorting.indexOf(hoditCards.charAt(i)),kolodaSorting.indexOf(tempProverkaCards.charAt(j)))){
                            tempOtbivaetCards = tempOtbivaetCards.substring(0,i)+tempProverkaCards.charAt(j) + tempOtbivaetCards.substring(i+1);
                            tempProverkaCards = tempProverkaCards.replaceAll(String.valueOf(tempProverkaCards.charAt(j)),"");
                            break;
                        }
//                        else {
//                            return false;
//                        }
                    }
                }

            }
        }
//        Log.d("ProverVse",tempOtbivaetCards.length() + " " + hoditCards.length() + " " +tempOtbivaetCards.indexOf(' '));
//Временно пока робот не отбивается с обратной стороны
        if(tempOtbivaetCards.length()!=hoditCards.length() || tempOtbivaetCards.indexOf(' ')!=-1) return false;
        return true;
    }
    private boolean funOtbivProverka(int h, int o){
        if (o % 13 > h % 13 && o/13==h/13) return true;
//        if (kolodaRandom.length()>20) return false;
        if (Math.random()*100>robotParams[3]) return false;
        if (o/13==koziri && h/13 != koziri) return true;
        return false;
    }
    boolean perevod(){
        if(!modGame) return  false;
        if(Math.random()*100>robotParams[0]) return false;
        if(hoditCards.length() == 6) return false;
        //сделать функцию перевода
        if((otbivaetCards == null || otbivaetCards.length() == 0) && hoditCards.length()>0) {
            if(lenOpponent > hoditCards.length()) {
                for(int i = 0; i < myCards.length();i++) {
                    if(kolodaSorting.indexOf(hoditCards.charAt(0))%13 == kolodaSorting.indexOf(myCards.charAt(i))%13 && kolodaSorting.indexOf(myCards.charAt(i))/13!=koziri) {

                        hoditCards = hoditCards + myCards.charAt(i);
                        myCards = myCards.replaceAll(String.valueOf(myCards.charAt(i)),"");
                        hodit = true;
                        return true;
                    }
                }

                for(int i = 0; i < myCards.length();i++) {
                    if (Math.random()*100 < robotParams[1]) {
                        if (kolodaSorting.indexOf(hoditCards.charAt(0)) % 13 == kolodaSorting.indexOf(myCards.charAt(i)) % 13) {

                            hoditCards = hoditCards + myCards.charAt(i);
                            myCards = myCards.replaceAll(String.valueOf(myCards.charAt(i)), "");
                            hodit = true;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    int robotHodit() {
        Log.d("Gluuuk","H " + hoditCards);

        if (iBery) return 10;
//        try {
//            Thread.sleep(1000); //Приостанавливает поток на 1 секунду
//        } catch (Exception e) {
//
//        }
        if (endGame) return 10;
        if(hodit) {
            if(myCards.length()==0 && kolodaRandom.length() == 0 && ALLOPPONCARD()>hoditCards.length()){
                return  99;
            }
            if(hoditCards== null || hoditCards.length() == 0) {
                if(nachalniiHod()) return 0;
            }
            else if (proverkaPodkod()){
                return 0;
            }
            else if (proverkaBito()){
                iniciatorBito = true;
                return 1;
            }
        }
        return 10;
    }
    int ALLOPPONCARD() {
        int otbCartNum = 0;
        for(int i = 0; i < otbivaetCards.length(); i++) {
            if(otbivaetCards.charAt(i) != ' '){
                otbCartNum++;
            }
        }
        Log.d("PodcidAll",lenOpponent + " " + otbCartNum + " | " + hoditCards.length() + " " + (lenOpponent + otbCartNum));
        return lenOpponent+otbCartNum;
    }
    boolean proverkaBito() {

        if (hoditCards==null || otbivaetCards==null) return false;
        if (hoditCards.length() == 0 || otbivaetCards.length() == 0) return false;
        if(otbivaetCards.length() == hoditCards.length()){
//            for(int i = 0; i<otbivaetCards.length();i++) {
//                if(kolodaSorting.indexOf(otbivaetCards.charAt(i)) == -1){
//                    return false;
//                }
//            }
            if(otbivaetCards.indexOf(' ')!=-1) return false;
            if(otbivaetCards.length()== 6) {

                for (int i = 0; i < hoditCards.length(); i++) {
                    myCards = myCards.replaceAll(String.valueOf(otbivaetCards.charAt(i)),"");
                    myCards = myCards.replaceAll(String.valueOf(hoditCards.charAt(i)),"");
                }
                //Назначить биту и сделать раздачу карт
                //Не забыть про смену ролей hodit = !hodit;
                rasdacha(kolodaRandom);

                return true;
            }

            for (int i = 0; i < hoditCards.length(); i++) {
                myCards = myCards.replaceAll(String.valueOf(otbivaetCards.charAt(i)),"");
                myCards = myCards.replaceAll(String.valueOf(hoditCards.charAt(i)),"");
            }
            //Назначить биту и сделать раздачу карт
            //Не забыть про смену ролей hodit = !hodit;
            rasdacha(kolodaRandom);
            return true;
        }
        return false;
    }
    boolean proverkaPodkod(){
//        проверить можно ли подкидывать при нехватки леноппонент
        if(hoditCards.length()>otbivaetCards.length()) return false;
        if(hoditCards.length() == ALLOPPONCARD()) {
            return false;
        }
        if(Math.random()*100>robotParams[5]) return false;
        // Для одиночного подкидования + избавит от ошибки перевода+подкид одновременно
        if(hoditCards.length() < 6){
            for (int i = 0; i < myCards.length(); i++) {
                if ((int)(kolodaSorting.indexOf(myCards.charAt(i))/13) != koziri || kolodaRandom.length()<10) {
                    for(int j = 0; j < hoditCards.length(); j++) {
                        Log.d("Podcid","hodit "+ j+ " " + (kolodaSorting.indexOf(myCards.charAt(i))%13 == kolodaSorting.indexOf(hoditCards.charAt(j))));
                        if(kolodaSorting.indexOf(myCards.charAt(i))%13 == kolodaSorting.indexOf(hoditCards.charAt(j))%13){
                            if(kolodaSorting.indexOf(myCards.charAt(i))/13 ==koziri && Math.random()*100>robotParams[6]) {
                                break;
                            }
                            hoditCards = hoditCards + myCards.charAt(i);
//                            myCards = myCards.substring(0,i) + myCards.substring(i+1);
                            myCards = myCards.replaceAll(String.valueOf(myCards.charAt(i)),"");
                            return true;
                        }
                    }
                    for(int j = 0; j < otbivaetCards.length(); j++) {
                        Log.d("Podcid","otbivaet "+ j + " " +(kolodaSorting.indexOf(myCards.charAt(i))%13 == kolodaSorting.indexOf(otbivaetCards.charAt(j))));
                        if(kolodaSorting.indexOf(myCards.charAt(i))%13 == kolodaSorting.indexOf(otbivaetCards.charAt(j))%13){
                            if(kolodaSorting.indexOf(myCards.charAt(i))/13 ==koziri && Math.random()*100>robotParams[6]) {
                                return false;
                            }
                            hoditCards = hoditCards + myCards.charAt(i);
//                            myCards = myCards.substring(0,i) + myCards.substring(i+1);
                            myCards = myCards.replaceAll(String.valueOf(myCards.charAt(i)),"");
                            return true;
                        }
                    }
                }
            }
            Log.d("Podcid","end false");
            return false;
        }
        return false;
    }
    boolean nachalniiHod(){
        int minCard = -1;
        int numMinCard = -1;
        for (int i = 0; i < myCards.length(); i++) {
            if ((int) (kolodaSorting.indexOf(myCards.charAt(i)) / 13) != koziri && koloda52.indexOf(myCards.charAt(i)) > minCard) {
                minCard = koloda52.indexOf(myCards.charAt(i));
                numMinCard = i;
            }
        }
        if (numMinCard != -1) {
            hoditCards = hoditCards + myCards.charAt(numMinCard);
//            myCards = myCards.substring(0,numMinCard) + myCards.substring(numMinCard+1);
            myCards = myCards.replaceAll(String.valueOf(myCards.charAt(numMinCard)),"");
        }
        else {
            for (int i = 0; i < myCards.length(); i++) {
                if (koloda52.indexOf(myCards.charAt(i)) > minCard) {
                    minCard = koloda52.indexOf(myCards.charAt(i));
                    numMinCard = i;
                }
            }
            hoditCards = hoditCards + myCards.charAt(numMinCard);
//            myCards = myCards.substring(0,minCard) + myCards.substring(minCard+1);
            myCards = myCards.replaceAll(String.valueOf(myCards.charAt(numMinCard)),"");
        }
        Log.d("BotFirsHod","minId " + minCard+ " HoditCard "+ hoditCards+" MyCards " + myCards );
        return true;
    }
    protected int sortingCard() {
        String tempString="";
        for (int i = 0; i < koziri*13; i++) {
            if(myCards.indexOf(kolodaSorting.charAt(i)) != -1) {
                tempString = tempString + kolodaSorting.charAt(i);
            }
        }
        for (int i = (koziri+1)*13; i < kolodaSorting.length(); i++){
            if(myCards.indexOf(kolodaSorting.charAt(i)) != -1) {
                tempString = tempString + kolodaSorting.charAt(i);
            }
        }
        for (int i = koziri*13; i < (koziri+1)*13; i++){
            if(myCards.indexOf(kolodaSorting.charAt(i)) != -1) {
                tempString = tempString + kolodaSorting.charAt(i);
            }
        }
        myCards = tempString;
        return 0;
    }
}
