// Importações:
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Classe base abstrata: Nota
abstract class Nota implements Serializable {

    protected String titulo;
    protected String conteudo;
    protected Date dataCriacao;
    protected Date dataAtualizacao;

    public Nota(String titulo, String conteudo) {
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.dataCriacao = new Date();
        this.dataAtualizacao = new Date();
    }

    public abstract void salvar();

    public abstract void excluir();

// Getters e Setters
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
        this.dataAtualizacao = new Date();
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
        this.dataAtualizacao = new Date();
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public Date getDataAtualizacao() {
        return dataAtualizacao;
    }
}

// Subclasse: notas de texto
class NotaTexto extends Nota {

    private String texto;

    public NotaTexto(String titulo, String texto) {
        super(titulo, "");
        this.texto = texto;
    }

    @Override
    public void salvar() {
    }

    @Override
    public void excluir() {
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
        this.dataAtualizacao = new Date();
    }
}

// Classe: itens do checklist
class ItemChecklist implements Serializable {

    private String descricao;
    private boolean conclusao;

    public ItemChecklist(String descricao) {
        this.descricao = descricao;
        this.conclusao = false;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isConclusao() {
        return conclusao;
    }

    public void setConclusao(boolean conclusao) {
        this.conclusao = conclusao;
    }
}

// Subclasse: checklists
class Checklist extends Nota {

    private List<ItemChecklist> itens;

    public Checklist(String titulo) {
        super(titulo, "");
        this.itens = new ArrayList<>();
    }

    public void marcarItem(int indice) {
        if (indice >= 0 && indice < itens.size()) {
            itens.get(indice).setConclusao(true);
            this.dataAtualizacao = new Date();
        }
    }

    public void desmarcarItem(int indice) {
        if (indice >= 0 && indice < itens.size()) {
            itens.get(indice).setConclusao(false);
            this.dataAtualizacao = new Date();
        }
    }

    public void adicionarItem(String descricao) {
        itens.add(new ItemChecklist(descricao));
        this.dataAtualizacao = new Date();
    }

    @Override
    public void salvar() {
    }

    @Override
    public void excluir() {
    }

    public List<ItemChecklist> getItens() {
        return itens;
    }
}

// Subclasse: DataComemorativa
class DataComemorativa extends Nota {

    private String ocasiao;
    private Date dataEvento;

    public DataComemorativa(String titulo, String ocasiao, Date dataEvento) {
        super(titulo, "");
        this.ocasiao = ocasiao;
        this.dataEvento = dataEvento;
    }

    @Override
    public void salvar() {
    }

    @Override
    public void excluir() {
    }

    public String getOcasiao() {
        return ocasiao;
    }

    public void setOcasiao(String ocasiao) {
        this.ocasiao = ocasiao;
        this.dataAtualizacao = new Date();
    }

// Getters e Setters para data do evento
    public Date getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(Date dataEvento) {
        this.dataEvento = dataEvento;
        this.dataAtualizacao = new Date();
    }

    public String getDataEventoFormatada() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/aaaa");
        return sdf.format(dataEvento);
    }
}

// Classe: Usuário
class Usuario implements Serializable {

    private String nome;
    private String senha;
    protected List<Nota> notas;

    public Usuario(String nome, String senha) {
        this.nome = nome;
        this.senha = senha;
        this.notas = new ArrayList<>();
    }

    public void criarNota(Nota nota) {
        notas.add(nota);
        salvarDados(); 
    }

    public void visualizarNotas() {
        System.out.println("\n--- Notas de " + nome + " ---");
        for (int i = 1; i < notas.size(); i++) {
            Nota nota = notas.get(i);
            System.out.println("[" + i + "] Título: " + nota.getTitulo());
            System.out.println("    Tipo: " + nota.getClass().getSimpleName());
            System.out.println("    Última atualização: " + nota.getDataAtualizacao());

            if (nota instanceof NotaTexto) {
                NotaTexto notaTexto = (NotaTexto) nota;
                System.out.println("    Conteúdo: " + notaTexto.getTexto());
            } else if (nota instanceof Checklist) {
                Checklist checklist = (Checklist) nota;
                System.out.println("    Itens: " + checklist.getItens().size() + " itens");
            } else if (nota instanceof DataComemorativa) {
                DataComemorativa dataComem = (DataComemorativa) nota;
                System.out.println("    Ocasião: " + dataComem.getOcasiao());
                System.out.println("    Data do Evento: " + dataComem.getDataEventoFormatada()); // NOVA LINHA
            }
            System.out.println("------------------------");
        }
    }

    public void excluirNota(int indice) {
        if (indice >= 1 && indice < notas.size()) {
            notas.remove(indice);
            salvarDados();
        }
    }

// Getter para a lista de notas
    public List<Nota> getNotas() {
        return notas;
    }

// Método para salvar dados em arquivo
    public void salvarDados() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("notas_" + senha + ".dat"))) {
            oos.writeObject(this);
            System.out.println("\nDados salvos automaticamente!\n");
        } catch (IOException e) {
            System.out.println("Erro ao salvar dados: " + e.getMessage());
        }
    }

// Método estático para carregar dados do arquivo
    public static Usuario carregarDados(String senha) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("notas_" + senha + ".dat"))) {
            return (Usuario) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("Nenhum dado anterior encontrado. Criando novo usuário.");
            return null;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao carregar dados: " + e.getMessage());
            return null;
        }
    }

// Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return senha;
    }

    public void setEmail(String senha) {
        this.senha = senha;
    }
}
