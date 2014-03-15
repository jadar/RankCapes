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
    var configuredArtifacts = getConfiguredArtifacts();

    data = data.data;

    artifact_index[artifact_index.length] = data;
    var node = data.nodeName;

    for (var artifact in data.children)
    {
        artifact = data.children[artifact];
        var versions = artifact.children;

        for (var version in versions)
        {
            version = versions[version];
            
            //console.log("version");
            var compiledArtifacts = {};
            var versionNum = version["version"];
            var versionArtifacts = version.children;
            
            for (var versionArtifact in versionArtifacts)
            {
                versionArtifact = versionArtifacts[versionArtifact];
                var versionArtifactName = versionArtifact["nodeName"];
                var downloadUrl = versionArtifact["artifactUri"];
                compiledArtifacts[versionArtifactName] = downloadUrl;
            }
            
            var v = versionNum.split("-");
            addBuildToPage(node, v[1], v[0], compiledArtifacts);
        }
    }
}

function addBuildToPage(artifactId, version, minecraft, versionArtifacts)
{
    var table = $("#"+artifactId);

    var html = "<tr><td>" + version +"</td><td>" + minecraft + "</td>";
    
    for (artifact in versionArtifacts)
    {
        artifact = String(artifact);
        html += "<td>";
        var value = versionArtifacts[artifact];
        
        var title = "Jar";
        
        if (stringContains(artifact, "javadoc"))
        {
            title = "Javadoc";
        }
        else if (stringContains(artifact, "src"))
        {
            title = "Src";
        }
        else if(stringContains(artifact, "deobj"))
        {
            title = "Deobfuscated";
        }
        
        
        html += "<a href='" + value + "'>" + title + "</a>";
        html += "</td>"
    }
    
    html += "</tr>";
    table.append(html);
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


function stringContains(string1, string2)
{
    return string1.indexOf(string2) == 1;
}