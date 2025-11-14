// Interface de terminal principal

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Date;
import java.util.Calendar;
import java.text.ParseException;

public class SistemaNotasTerminal {
    private static Usuario usuarioAtual;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        inicializarSistema();
        exibirMenuPrincipal();
    }

    //horário
    private static String getSaudacao() {
        Calendar calendario = Calendar.getInstance();
        int hora = calendario.get(Calendar.HOUR_OF_DAY);
        
        if (hora >= 5 && hora < 12) {
            return "Bom dia";
        } else if (hora >= 12 && hora < 18) {
            return "Boa tarde";
        } else {
            return "Boa noite";
        }
    }

    private static void inicializarSistema() {
        String saudacao = getSaudacao(); // NOVA SAUDAÇÃO
        System.out.println("\n=== " + saudacao + "! BEM-VINDO AO NOPAPER! ===");
        System.out.print("Digite sua senha (ou crie uma nova): ");
        String senha = scanner.nextLine();
        
        // Tentar carregar dados existentes
        usuarioAtual = Usuario.carregarDados(senha);
        
        if (usuarioAtual == null) {
            // Se não existir, criar novo usuário
            System.out.print("Digite seu nome: ");
            String nome = scanner.nextLine();
            usuarioAtual = new Usuario(nome, senha);
            System.out.println("\nNovo usuário criado! " + saudacao + ", " + nome + "!");
        } else {
            System.out.println("\nDados carregados! " + saudacao + ", " + usuarioAtual.getNome() + "!");
            System.out.println("Você tem " + usuarioAtual.getNotas().size() + " notas salvas.");
        }
    }

    private static void exibirMenuPrincipal() {
        while (true) {
            System.out.println("\n=== MENU PRINCIPAL ===\n");
            System.out.println("1. Criar nova anotação");
            System.out.println("2. Visualizar todas as notas");
            System.out.println("3. Excluir nota");
            System.out.println("4. Gerenciar checklist");
            System.out.println("5. Salvar dados manualmente");
            System.out.println("6. Sair\n");
            System.out.print("Escolha uma opção: ");

            int opcao = lerInteiro();

            switch (opcao) {
                case 1:
                    criarNovaNota();
                    break;
                case 2:
                    visualizarNotasDetalhadas();
                    break;
                case 3:
                    excluirNota();
                    break;
                case 4:
                    gerenciarChecklist();
                    break;
                case 5:
                    usuarioAtual.salvarDados();
                    break;
                case 6:
                    System.out.println("Salvando dados...");
                    usuarioAtual.salvarDados();
                    System.out.println("Saindo do sistema... Até logo!");
                    return;
                default:
                    System.out.println("Opção inválida! Digite 1, 2, 3, 4, 5, ou 6.");
            }
        }
    }

    private static void criarNovaNota() {
        System.out.println("\n=== CRIAR NOVA ANOTAÇÃO ===\n");
        System.out.println("1. Nota de Texto Simples");
        System.out.println("2. Checklist");
        System.out.println("3. Data Comemorativa\n");
        System.out.print("Escolha o tipo de nota: ");

        int tipo = lerInteiro();

        System.out.print("Título da nota: ");
        String titulo = scanner.nextLine();

        switch (tipo) {
            case 1:
                System.out.print("Conteúdo da nota: ");
                String texto = scanner.nextLine();
                NotaTexto notaTexto = new NotaTexto(titulo, texto);
                usuarioAtual.criarNota(notaTexto);
                System.out.println(" Nota de texto criada com sucesso!");
                break;

            case 2:
                Checklist checklist = new Checklist(titulo);
                System.out.println("Adicionando itens ao checklist (digite 'fim' para parar):");
                
                while (true) {
                    System.out.print("Digite um item: ");
                    String item = scanner.nextLine();
                    if (item.equalsIgnoreCase("fim")) break;
                    checklist.adicionarItem(item);
                }
                usuarioAtual.criarNota(checklist);
                System.out.println(" Checklist criado com sucesso!");
                break;

            case 3:
                System.out.print("Digite a ocasião: ");
                String ocasiao = scanner.nextLine();
                
                // NOVO: Solicitar data do evento
                Date dataEvento = null;
                while (dataEvento == null) {
                    System.out.print("Digite a data do evento (DD/MM/AAAA): ");
                    String dataStr = scanner.nextLine();
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        sdf.setLenient(false); // Não permitir datas inválidas
                        dataEvento = sdf.parse(dataStr);
                    } catch (ParseException e) {
                        System.out.println("Data inválida! Use o formato DD/MM/AAAA.");
                    }
                }
                
                DataComemorativa dataComem = new DataComemorativa(titulo, ocasiao, dataEvento);
                usuarioAtual.criarNota(dataComem);
                System.out.println(" Data comemorativa criada com sucesso!");
                break;

            default:
                System.out.println("Tipo de nota inválido!");
        }
    }

    private static void visualizarNotasDetalhadas() {
        System.out.println("\n=== SUAS NOTAS ===");
        
        List<Nota> notas = usuarioAtual.getNotas();

        if (notas.isEmpty()) {
            System.out.println("Nenhuma nota encontrada.");
            return;
        }

        for (int i = 0; i < notas.size(); i++) {
            Nota nota = notas.get(i);
            System.out.println("\n[" + i + "] " + nota.getTitulo());
            System.out.println("Tipo: " + nota.getClass().getSimpleName());
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            System.out.println("Criada em: " + sdf.format(nota.getDataCriacao()));
            System.out.println("Atualizada em: " + sdf.format(nota.getDataAtualizacao()));

            if (nota instanceof NotaTexto) {
                NotaTexto notaTexto = (NotaTexto) nota;
                System.out.println("Conteúdo: " + notaTexto.getTexto());
            } else if (nota instanceof Checklist) {
                Checklist checklist = (Checklist) nota;
                System.out.println("Itens:");
                List<ItemChecklist> itens = checklist.getItens();
                for (int j = 0; j < itens.size(); j++) {
                    ItemChecklist item = itens.get(j);
                    String status = item.isConclusao() ? "[]" : "[ ]";
                    System.out.println("  " + j + ". " + status + " " + item.getDescricao());
                }
            } else if (nota instanceof DataComemorativa) {
                DataComemorativa dataComem = (DataComemorativa) nota;
                System.out.println("Ocasião: " + dataComem.getOcasiao());
                // NOVA LINHA: Mostrar data do evento formatada
                System.out.println("Data do Evento: " + dataComem.getDataEventoFormatada());
            }
            System.out.println("---");
        }
    }

    private static void excluirNota() {
        List<Nota> notas = usuarioAtual.getNotas();
        
        if (notas.isEmpty()) {
            System.out.println("Não há notas para excluir.");
            return;
        }
        
        System.out.println("\n=== EXCLUIR NOTA ===\n");
        for (int i = 0; i < notas.size(); i++) {
            System.out.println("[" + i + "] " + notas.get(i).getTitulo());
        }
        
        System.out.print("Digite o número da nota a ser excluída: ");
        int indice = lerInteiro();

        if (indice >= 0 && indice < notas.size()) {
            usuarioAtual.excluirNota(indice);
            System.out.println(" Nota excluída com sucesso!");
        } else {
            System.out.println("Número de nota inválido!");
        }
    }

    private static void gerenciarChecklist() {
        System.out.println("\n=== GERENCIAR CHECKLIST ===\n");
        
        List<Nota> notas = usuarioAtual.getNotas();

        // Encontrar checklists
        List<Checklist> checklists = new ArrayList<>();
        for (Nota nota : notas) {
            if (nota instanceof Checklist) {
                checklists.add((Checklist) nota);
            }
        }

        if (checklists.isEmpty()) {
            System.out.println("Nenhum checklist encontrado.");
            return;
        }

        // Listar checklists
        System.out.println("Seus checklists:");
        for (int i = 0; i < checklists.size(); i++) {
            System.out.println(i + ". " + checklists.get(i).getTitulo());
        }

        System.out.print("Escolha um checklist: ");
        int checklistIndex = lerInteiro();

        if (checklistIndex >= 0 && checklistIndex < checklists.size()) {
            Checklist checklist = checklists.get(checklistIndex);
            gerenciarChecklistIndividual(checklist);
        } else {
            System.out.println("Checklist inválido!");
        }
    }

    private static void gerenciarChecklistIndividual(Checklist checklist) {
        while (true) {
            System.out.println("\n=== " + checklist.getTitulo() + " ===\n");
            List<ItemChecklist> itens = checklist.getItens();
            
            if (itens.isEmpty()) {
                System.out.println("Nenhum item neste checklist.");
            } else {
                for (int i = 0; i < itens.size(); i++) {
                    ItemChecklist item = itens.get(i);
                    String status = item.isConclusao() ? "[X]" : "[ ]";
                    System.out.println(i + ". " + status + " " + item.getDescricao());
                }
            }

            System.out.println("\n1. Marcar item");
            System.out.println("2. Desmarcar item");
            System.out.println("3. Adicionar item");
            System.out.println("4. Voltar");
            System.out.print("Escolha uma opção: ");

            int opcao = lerInteiro();

            switch (opcao) {
                case 1:
                    if (!itens.isEmpty()) {
                        System.out.print("Digite o número do item a marcar: ");
                        int itemMarcar = lerInteiro();
                        if (itemMarcar >= 0 && itemMarcar < itens.size()) {
                            checklist.marcarItem(itemMarcar);
                            usuarioAtual.salvarDados(); // Salva após modificar
                            System.out.println("Item marcado!");
                        } else {
                            System.out.println("Número de item inválido!");
                        }
                    } else {
                        System.out.println("Não há itens para marcar.");
                    }
                    break;
                case 2:
                    if (!itens.isEmpty()) {
                        System.out.print("Digite o número do item a desmarcar: ");
                        int itemDesmarcar = lerInteiro();
                        if (itemDesmarcar >= 0 && itemDesmarcar < itens.size()) {
                            checklist.desmarcarItem(itemDesmarcar);
                            usuarioAtual.salvarDados(); // Salva após modificar
                            System.out.println("Item desmarcado!");
                        } else {
                            System.out.println("Número de item inválido!");
                        }
                    } else {
                        System.out.println("Não há itens para desmarcar.");
                    }
                    break;
                case 3:
                    System.out.print("Digite o novo item: ");
                    scanner.nextLine(); // Consumir quebra de linha
                    String novoItem = scanner.nextLine();
                    checklist.adicionarItem(novoItem);
                    usuarioAtual.salvarDados(); // Salva após adicionar
                    System.out.println("Item adicionado!");
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private static int lerInteiro() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Por favor, digite um número válido: ");
            }
        }
    }
}