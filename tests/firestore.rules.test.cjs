const {readFileSync}=require('node:fs');
const {test,before,after}=require('node:test');
const {initializeTestEnvironment,assertSucceeds,assertFails}=require('@firebase/rules-unit-testing');
const {doc,setDoc,getDoc,updateDoc,collection,query,where,getDocs}=require('firebase/firestore');
let env;
before(async()=>{
  env=await initializeTestEnvironment({projectId:'demo-aguia-portfolio',firestore:{host:'127.0.0.1',port:8085,rules:readFileSync('firestore.rules','utf8')}});
  await env.withSecurityRulesDisabled(async context=>{
    const db=context.firestore();
    for(const [uid,tipo] of [['op','operador'],['op2','operador'],['manager','gestor'],['leader','lider']]){
      await setDoc(doc(db,'users',uid),{uid,nome:'Pessoa Demo',email:uid+'@example.com',tipo});
    }
    for(const [id,usuarioId] of [['mine','op'],['other','op2']]){
      await setDoc(doc(db,'ideas',id),{titulo:'Ideia',descricao:'Teste',status:'Pendente',prioridade:'Normal',usuarioId});
    }
    await setDoc(doc(db,'projects','project1'),{nome:'Projeto fictício',progresso:'0'});
  });
});
after(async()=>{if(env)await env.cleanup();});
const db=uid=>env.authenticatedContext(uid).firestore();
test('visitante não acessa dados',async()=>{
  await assertFails(getDoc(doc(env.unauthenticatedContext().firestore(),'ideas','mine')));
});
test('cadastro permite operador e rejeita autoatribuição de gestor',async()=>{
  await assertSucceeds(setDoc(doc(db('new'),'users','new'),{uid:'new',nome:'Demo',email:'new@example.com',tipo:'operador'}));
  await assertFails(setDoc(doc(db('elevated'),'users','elevated'),{uid:'elevated',nome:'Demo',email:'demo@example.com',tipo:'gestor'}));
});
test('usuário não altera seu próprio perfil de acesso',async()=>{
  await assertFails(updateDoc(doc(db('op'),'users','op'),{tipo:'lider'}));
});
test('operador vê suas ideias e consulta com filtro de proprietário',async()=>{
  await assertSucceeds(getDoc(doc(db('op'),'ideas','mine')));
  await assertFails(getDoc(doc(db('op'),'ideas','other')));
  await assertSucceeds(getDocs(query(collection(db('op'),'ideas'),where('usuarioId','==','op'))));
  await assertFails(getDocs(collection(db('op'),'ideas')));
});
test('operador cadastra apenas ideia própria, pendente e de prioridade normal',async()=>{
  const idea={titulo:'Economia',descricao:'Demonstração',status:'Pendente',prioridade:'Normal',usuarioId:'op'};
  await assertSucceeds(setDoc(doc(db('op'),'ideas','new-idea'),idea));
  await assertFails(setDoc(doc(db('op'),'ideas','forged-owner'),{...idea,usuarioId:'op2'}));
  await assertFails(setDoc(doc(db('op'),'ideas','forged-status'),{...idea,status:'Aprovada'}));
});
test('gestor aprova ideia sem trocar proprietário',async()=>{
  await assertSucceeds(updateDoc(doc(db('manager'),'ideas','mine'),{status:'Aprovada',prioridade:'Alta'}));
  await assertFails(updateDoc(doc(db('manager'),'ideas','mine'),{usuarioId:'manager'}));
});
test('projetos são restritos a gestor e liderança',async()=>{
  await assertSucceeds(getDoc(doc(db('leader'),'projects','project1')));
  await assertSucceeds(updateDoc(doc(db('manager'),'projects','project1'),{progresso:'50'}));
  await assertFails(getDoc(doc(db('op'),'projects','project1')));
  await assertFails(updateDoc(doc(db('leader'),'projects','project1'),{progresso:'90'}));
});
test('somente liderança escreve estratégias',async()=>{
  await assertSucceeds(setDoc(doc(db('leader'),'strategies','strategy1'),{titulo:'Plano',descricao:'Demonstração'}));
  await assertSucceeds(getDoc(doc(db('op'),'strategies','strategy1')));
  await assertFails(setDoc(doc(db('manager'),'strategies','forged'),{titulo:'Não permitido'}));
});
