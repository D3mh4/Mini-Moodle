package modele;
public class Profile {

    private String prenom;
    private String nom;
    private String courriel; // lecture seule si on prefere
    private String motDePasse;
    private String telephone;
    private String imageIcone; // optionnel


    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCourriel() {
        return courriel;
    }
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getImageIcone() {
        return imageIcone;
    }

    public void setImageIcone(String imageIcone) {
        this.imageIcone = imageIcone;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
}
