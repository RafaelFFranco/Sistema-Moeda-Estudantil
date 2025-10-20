document.addEventListener('DOMContentLoaded', function(){
  // delegate clicks on .confirm-delete
  document.body.addEventListener('click', function(e){
    var el = e.target.closest && e.target.closest('.confirm-delete');
    if(!el) return;
    e.preventDefault();
    var href = el.getAttribute('href');
    showDeleteModal(function(){ window.location = href; });
  });

  // modal close
  document.body.addEventListener('click', function(e){
    if(e.target.matches('.modal-backdrop') || e.target.closest('.btn-cancel')){
      closeModal();
    }
  });

  // search submit on Enter
  var searchInput = document.getElementById('search-input');
  if(searchInput){
    searchInput.addEventListener('keydown', function(e){
      if(e.key === 'Enter'){
        e.preventDefault();
        performSearch(searchInput.value);
      }
    });
  }

  // optional: clicking a search icon in the header if present
  document.body.addEventListener('click', function(e){
    var el = e.target.closest && e.target.closest('[data-action="search"]');
    if(!el) return;
    var inp = document.getElementById('search-input');
    if(inp) performSearch(inp.value);
  });

});

function showDeleteModal(onConfirm){
  if(document.querySelector('.modal-backdrop')) return;
  var backdrop = document.createElement('div');
  backdrop.className = 'modal-backdrop fade-in';
  backdrop.innerHTML = `
    <div class="modal" role="dialog" aria-modal="true">
      <h3>Confirmação</h3>
      <p>Tem certeza que deseja excluir este registro? Esta ação não pode ser desfeita.</p>
      <div class="modal-actions">
        <button class="btn btn-secondary btn-cancel">Cancelar</button>
        <button class="btn btn-primary btn-confirm">Confirmar</button>
      </div>
    </div>
  `;
  document.body.appendChild(backdrop);
  backdrop.querySelector('.btn-confirm').addEventListener('click', function(){ closeModal(); onConfirm && onConfirm(); });
}

function closeModal(){
  var b = document.querySelector('.modal-backdrop');
  if(b) b.remove();
}

function performSearch(q){
  q = (q || '').trim();
  // detect current path: if we are on alunos or empresas, reuse; else go to alunos
  var path = window.location.pathname;
  if(!path || path === '/' ) path = '/alunos';
  if(!path.startsWith('/alunos') && !path.startsWith('/empresas')) path = '/alunos';
  var url = path + (q ? ('?q=' + encodeURIComponent(q)) : '');
  window.location = url;
}
