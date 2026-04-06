package modele;

public class Quiz {
    private String titre;
    private int nbrQuestions;
    private String statut; //non commence ou termine
    private int score;
    private int resultat;

    public Quiz() {
    }

    public Quiz(String titre, int nbrQuestions, String statut) {
        this.titre = titre;
        this.nbrQuestions = nbrQuestions;
        this.statut = statut;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public int getNbrQuestions() {
        return nbrQuestions;
    }

    public void setNbrQuestions(int nbrQuestions) {
        this.nbrQuestions = nbrQuestions;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getResultat() {
        return resultat;
    }

    public void setResultat(int resultat) {
        this.resultat = resultat;
    }
}
