const GITHUB_URI = 'https://api.github.com';
import * as Rx from 'rxjs'

export const getGithubRepositories = () => Rx.from(
  fetch(`${GITHUB_URI}/repositories`)
    .then(r => r.json())
    .then(items => items.map(i => ({
      name: i.name,
      owner: i.owner.login,
      id: i.id,
      key: `${i.id}`,
      url: i.html_url
    })))
);
