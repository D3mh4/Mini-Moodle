package modele;

public class Question {
    private String enonce;
    private String type;
    private String[] choix;
    private String reponse;

    public Question(String enonce, String type, String[] choix, String reponse) {
        this.enonce = enonce;
        this.type = type;
        this.choix = choix;
        this.reponse = reponse;
    }

    public String getEnonce() {
        return enonce;
    }

    public String getType() {
        return type;
    }

    public String[] getChoix() {
        return choix;
    }

    public String getReponse() {
        return reponse;
    }
}