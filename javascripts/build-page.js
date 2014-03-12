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
    repoUrl = repoUrl + '/service/local/repositories/' + repositoryId + '/index_content/' + (groupId.replace(/\./g, "/")) + '/';
    $.ajax({
        type: 'GET',
        url: repoUrl,
        processData: true,
        data: {},
        dataType: "json",
        success: 
            function(data) {
                processData(data);
            }
    });
    
//    $.getJSON(repoUrl, function(data)
//    {
//        console.log("success!");
//        console.loc(data);
//    });
}

function processData(data)
{
    console.log(data);
}

function onPageLoad()
{
    var config = getPageConfiguration();
    var artifacts = getConfiguredArtifacts();
    if(config == null || artifacts == null) 
        return;
    
    var index = {};
    var i = 0;
    for(var artifact in artifacts)
    {
        artifact = artifacts[artifact];
        index[i] = getIndexContents(config["repo-url"], artifact["repo"], artifact["groupId"], artifact["artifactId"]);
        i++;
    }
    
}
$(document).ready(onPageLoad);
