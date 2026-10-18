# Configuração Firebase e perfis

Use um projeto Firebase próprio para experimentar. As configurações reais do projeto original não fazem parte deste repositório.

## Preparar

1. Cadastre um aplicativo Android com pacote `com.nordhub.aguiachallenge`.
2. Habilite login por e-mail/senha e crie um banco Cloud Firestore.
3. Substitua o exemplo por seu `app/google-services.json`.
4. Na raiz deste repositório, execute `npm.cmd ci`. Faça login no CLI Firebase e selecione explicitamente o seu projeto.
5. Depois de revisar o destino, execute `npx firebase deploy --only firestore:rules --project SEU_PROJECT_ID`. Esse passo altera apenas o projeto que você informar; não é feito automaticamente pelo aplicativo.

## Atribuir acesso

O cadastro cria `users/{uid}` com `uid`, `nome`, `email` e `tipo: operador`. Para promover uma conta, um administrador confiável altera `tipo` no console Firebase para `gestor` ou `lider`. Nunca permita que o cliente escreva seu próprio perfil de acesso.

As regras limitam o operador às suas ideias; o gestor avalia ideias e escreve projetos; a liderança lê os projetos e escreve estratégias. Alterações de perfil ficam bloqueadas ao cliente, inclusive para os perfis de gestão.

## Coleções

| Coleção | Conteúdo |
|---|---|
| `users` | Cadastro e perfil de acesso |
| `ideas` | Propostas, proprietário, status e prioridade |
| `projects` | Nome, investimento, retorno, prazo e progresso |
| `strategies` | Título e descrição da estratégia |

## Validação de fluxos

Em um ambiente próprio, cadastre três contas fictícias e atribua um perfil a cada uma. Verifique o percurso ideia → avaliação → projeto → dashboard, bem como recusas de acesso entre perfis. Os testes automatizados de regras não substituem essa validação de interface.

Referências: [condições das regras](https://firebase.google.com/docs/firestore/security/rules-conditions) e [testes com emulador](https://firebase.google.com/docs/firestore/security/test-rules-emulator).
