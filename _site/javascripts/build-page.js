function getPageConfiguration()
{
    var config = {};
    
    config = JSON.parse($("input[name='config']").val());
    
    return config;
}

function getConfiguredArtifacts()
{
    var artifacts = {};
    
    artifacts = JSON.parse($("input[name='builds-artifacts']").val());

    return artifacts;
}

function getIndexContents(repoUrl, repositoryId, groupId, artifactId)
{
    repoUrl = repoUrl.replace(/http/g, '');
    repoUrl = repoUrl.replace(/https/g, '');
    repoUrl = repoUrl.replace(/\:\/\//g, '');
    repoUrl = repoUrl + '/service/local/repositories/' + repositoryId + '/index_content/' + (groupId.replace(/\./g, "/")) + '/';
    repoUrl = "http://www.corsproxy.com/" + repoUrl;

    $.getJSON(
    repoUrl, 
    function(data)
    {
        indexContentsCallback(data);
    });
}

var artifact_index = {};
function indexContentsCallback(data)
{
    artifact_index[artifact_index.length] = data;
}

function onPageLoad()
{
    var config = getPageConfiguration();
    var artifacts = getConfiguredArtifacts();
    if(config == null || artifacts == null) 
        return;
    
    var i = 0;
    for(var artifact in artifacts)
    {
        artifact = artifacts[artifact];
        getIndexContents(config["repo-url"], artifact["repo"], artifact["groupId"], artifact["artifactId"]);
    }
    
}
$(document).ready(onPageLoad);
