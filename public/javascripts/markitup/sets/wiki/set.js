// ----------------------------------------------------------------------------
// markItUp!
// ----------------------------------------------------------------------------
// Copyright (C) 2008 Jay Salvat
// http://markitup.jaysalvat.com/
// ----------------------------------------------------------------------------
mySettings = {
    nameSpace:          "wiki", // Useful to prevent multi-instances CSS conflict
    previewParserPath:  "/Application/preview",
    onShiftEnter:       {keepDefault:false, replaceWith:'\n\n'},
    markupSet:  [
        //{name:'Heading 1', className:'markItUpButton1', key:'1', openWith:'== ', closeWith:' ==', placeHolder:'Your title here...' },
        //{name:'Heading 2', className:'markItUpButton2', key:'2', openWith:'=== ', closeWith:' ===', placeHolder:'Your title here...' },
        {name:'Heading 3', className:'markItUpButton3', key:'3', openWith:'==== ', closeWith:' ====', placeHolder:'Your title here...' },
        {name:'Heading 4', className:'markItUpButton4', key:'4', openWith:'===== ', closeWith:' =====', placeHolder:'Your title here...' },
        {name:'Heading 5', className:'markItUpButton5', key:'5', openWith:'====== ', closeWith:' ======', placeHolder:'Your title here...' },
        {separator:'---------------' },        
        {name:'Bold', className:'markItUpButton6', key:'B', openWith:"**", closeWith:"**"}, 
        {name:'Italic', className:'markItUpButton7', key:'I', openWith:"//", closeWith:"//"}, 
        {name:'Stroke through', className:'markItUpButton8', key:'S', openWith:'<s>', closeWith:'</s>'}, 
        {separator:'---------------' },
        {name:'Bulleted list', className:'markItUpButton9', openWith:'(!(* |!|*)!)'}, 
        {name:'Numeric list', className:'markItUpButton10', openWith:'(!(# |!|#)!)'}, 
        {separator:'---------------' },
        {name:'Picture', className:'markItUpButton11', key:'P', replaceWith:'{{[![Url:!:http://]!]|[![name]!]}}'}, 
        //{name:'Link', className:'markItUpButton12', key:'L', openWith:'[[[![Link]!]|[![name]!]]] '},
        {name:'Url', className:'markItUpButton13', openWith:'[[![Url:!:http://]!] ', closeWith:']', placeHolder:'Your text to link here...' },
        {separator:'---------------' },
        {name:'Quotes', className:'markItUpButton14', openWith:'(!(> |!|>)!)'},
        {name:'Code', className:'markItUpButton15', openWith:'{{{', closeWith:'}}}'}, 
        {separator:'---------------' },
        {name:'Preview', call:'preview', className:'preview'}
    ]
}