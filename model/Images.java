package ComparadorDeImagens.model;

public class Images {

    private String _datatime;
    private String imagem;

    public Images(String _datatime, String imagem) {
        this._datatime = _datatime;
        this.imagem = imagem;
    }

    public String getDatatime() {
        return _datatime;
    }

    public String getImagem() {
        return imagem;
    }

    public void setDatatime(String _datatime) {
        this._datatime = _datatime;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }
    
    
}
